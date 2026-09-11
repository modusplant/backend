# Performance Test Architecture

Architecture reference for how nGrinder is deployed and behaves on this host, and how to
investigate a run directly from the filesystem/containers; consumed on demand, not
path-auto-attached. Scripts under `.claude/scripts/*.groovy` are nGrinder Groovy scripts, not part
of the Gradle build.

---

## 1. Environment Topology

- Controller and agent run as separate Docker containers: `ngrinder-controller` and
  `ngrinder-agent-1`.
- Controller-side per-test data lives at
  `/srv/ngrinder/ngrinder-controller/perftest/<idRangeFolder>/<testId>/` (e.g. `1000_1999/1060/`):
  - `logs/` — one worker log zip per agent, named `<agentContainerIdPrefix>--log.zip` (e.g.
    `f12b06fb76df--log.zip`), containing per-process log files (`<agentContainerIdPrefix>-0.log`
    for process 0, etc.).
  - `report/` — `output.csv` plus one `.data` file per reported metric (`TPS.data`, `Errors.data`,
    `Mean_Test_Time_(ms).data`, ...).
- Controller and agent runtime logs (test lifecycle events, grinder properties, stop/finish
  events) are read via `docker logs ngrinder-controller` / `docker logs ngrinder-agent-1` —
  neither container writes an additional file-based log at a shallow, easily-found path, so
  `docker logs` is the primary source for anything not already captured in the worker log zip.

## 2. Compile-Checking a Script Without a Live Run

The host's default JDK is too new for the bundled script engine: Groovy 3.0.5's bundled ASM
cannot parse JDK 21 bytecode, and the real nGrinder agent itself runs on JDK 11. To validate a
script compiles without actually running a test:

1. Use JDK 11 (e.g. `~/.jdks/corretto-11.0.32.1/bin/java`), not the host's default JDK.
2. Build the classpath from the controller's own bundled jars (these are the exact versions the
   real agent/worker uses), found at
   `/srv/ngrinder/ngrinder-controller/lib/ngrinder-controller-3.5.9-p1.war-spring-boot-libs-*/`
   with `find <dir> -maxdepth 1`, matching: `grinder-*3.9.1*.jar`, `groovy-*3.0.5*.jar`,
   `ngrinder-*3.5.9-p1*.jar`, `junit-4.13.1.jar`, `hamcrest-*.jar`.
3. Invoke: `<jdk11>/bin/java -cp <classpath> org.codehaus.groovy.tools.FileSystemCompiler -d
   <outdir> -cp <classpath> <script.groovy>`. Exit code 0 and the expected `.class` files confirm
   the script is syntactically and semantically valid against the real runtime's library versions.

## 3. Statistics Collection Requires an Explicit `GTest.record()` Binding

A `GTest` instance must be constructed (typically once, in `@BeforeProcess`) and bound via
`.record(httpRequestInstance)` to every `HTTPRequest` instance whose calls should count toward
nGrinder's reported statistics — typically done in `@BeforeThread`, since `HTTPRequest` instances
are usually thread-local fields. Without this binding, HTTP calls made through that
`HTTPRequest` instance still execute and can succeed, but nGrinder records zero statistics for
them, surfacing as `"Total Statistics is {}"` and the test being reported as failed/abnormal even
though the underlying calls worked. Only bind the `HTTPRequest` instances whose traffic is meant
to be measured — leave setup/login `HTTPRequest` instances unbound to keep a Test's statistics
pure (no login/setup latency mixed into the measured API's numbers).

## 4. A Recorded `HTTPRequest`'s Convenience Method Overloads Can Double-Count

`org.ngrinder.http.HTTPRequest` implements per-verb interfaces (e.g. `HTTPPost`) that declare one
concrete method per verb (`POST(String, byte[], List<Header>)`) plus several convenience **default
interface methods** (e.g. `POST(String, byte[])`) whose entire body just delegates to the concrete
one (`return this.POST(url, bytes, Collections.emptyList());`). When a `GTest.record(httpRequest)`
binding (Section 3) is applied with no method filter, Grinder's DCR instrumentation attaches to
**every** instrumentable method reachable on that object — including both a default convenience
method and the concrete method it delegates to. Calling the convenience overload therefore passes
one logical request through two separately-instrumented method frames, and the console's `Tests`
statistic counts both, reporting ~2× the real call count, even though the actual HTTP call is
issued exactly once inside the concrete method regardless of which overload is entered.

**Evidence**: per-endpoint HTTP response-line counts in the worker log match the real call count
exactly; the controller-reported `Tests` aggregate does not.

**Fix**: on a `.record()`-bound `HTTPRequest`, always call the concrete, most-specific overload
directly (e.g. `POST(url, bytes, headers)`, not `POST(url, bytes)`) so exactly one instrumented
method frame is entered per logical call. Building the `headers` list explicitly
(`org.apache.hc.core5.http.message.BasicHeader`, from `httpcore5-5.0.3.jar` on the worker
classpath) instead of relying on `setHeaders()` + a convenience overload avoids the double-count
at the source.

## 5. Prefer Duration Mode Over Run Count Mode

- **Run Count mode** (`grinder.runs=N`; the console removes `grinder.duration` from the worker's
  properties entirely) can be terminated early by an explicit stop message sent from the
  controller to the agent, before every thread reaches its assigned run count — visible in the
  worker log as `"finished <N> runs"` with N less than the configured Run Count on every thread
  simultaneously, and in the agent log as `"received a stop message"` immediately before
  shutdown, with no script exception or `SCRIPT_ERROR` involved.
- **Duration mode** (`grinder.duration=<ms>`, `grinder.runs=0`) runs to full, correct completion
  reliably when given adequate margin over the script's actual expected wall time (real per-thread
  setup time + intended load-profile duration).
- The Run Count early-stop is suspected to be a downstream symptom of the Section 4
  double-counting bug rather than an independent limitation of Run Count mode: the controller's
  completion check most likely compares its received cumulative test count against
  `grinder.runs × grinder.threads`, and an inflated received count would cross that target well
  before every thread's real progress does. This root cause is unconfirmed.
- **Default**: use Duration mode, sized generously over the script's actual expected wall time,
  until Run Count mode has been re-verified on a script carrying the Section 4 fix.

## 6. Diagnosing a Live Run From the Host

1. Find the test's grinder properties and lifecycle events:
   `docker logs ngrinder-controller | grep '\[<testId>\]'`.
2. Extract the worker log for line-level detail:
   `unzip /srv/ngrinder/ngrinder-controller/perftest/<idRangeFolder>/<testId>/logs/*.log.zip -d
   <destination>`, then `grep` the script's own `grinder.logger.info(...)` lines and the HTTP
   response lines (`grep -c "<endpoint> -> 200"` for a hard count of successful calls to a given
   endpoint).
3. Check `docker logs ngrinder-agent-1` for stop/shutdown events and their timing relative to the
   controller's own lifecycle log lines.
4. Check `<testId>/report/output.csv` and the per-metric `.data` files for the console's own
   aggregated view — cross-check against the worker log's raw call counts per Section 5 above
   before trusting it as the delivered request count.
