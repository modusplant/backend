# Performance Test Guide

How to write, compile-check, and investigate nGrinder performance tests on this host.
nGrinder Groovy scripts are not stored in this repository and are not part of the Gradle build.

---

## 1. Environment

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
  events) are read via `docker logs ngrinder-controller` / `docker logs ngrinder-agent-1`;
  neither container writes a file-based log at an easily found path.

---

## 2. Writing Scripts

### 2.1 Bind Measured Requests With `GTest.record()`

Construct a `GTest` once in `@BeforeProcess` and bind it via `.record(httpRequestInstance)` to
every `HTTPRequest` whose calls should count toward the statistics, typically in `@BeforeThread`
since `HTTPRequest` instances are usually thread-local fields. An unbound `HTTPRequest` still
executes its calls, but nGrinder records nothing for them, surfacing as `"Total Statistics is {}"`
and a failed/abnormal test. Leave setup/login `HTTPRequest` instances unbound so their latency
stays out of the measured API's numbers.

### 2.2 Call the Concrete `HTTPRequest` Overload

On a `.record()`-bound `HTTPRequest`, always call the concrete, most-specific overload
(e.g. `POST(url, bytes, headers)`, not `POST(url, bytes)`), because the convenience overloads are
default interface methods that delegate to the concrete one and Grinder instruments both, so the
console's `Tests` statistic counts each call twice. Build the `headers` list explicitly with
`org.apache.hc.core5.http.message.BasicHeader` (from `httpcore5-5.0.3.jar` on the worker classpath)
instead of using `setHeaders()` with a convenience overload.

### 2.3 Use Duration Mode

Use Duration mode (`grinder.duration=<ms>`, `grinder.runs=0`), sized generously over the script's
actual expected wall time (per-thread setup time + intended load-profile duration), because Run
Count mode (`grinder.runs=N`) can be stopped early by the controller before every thread reaches
its run count. The early stop shows up in the worker log as `"finished <N> runs"` with N below the
configured count on every thread at once, and in the agent log as `"received a stop message"`
right before shutdown, with no script exception or `SCRIPT_ERROR`.

---

## 3. Compile-Checking a Script Without a Live Run

The host's default JDK is too new for the bundled script engine: Groovy 3.0.5's bundled ASM
cannot parse JDK 21 bytecode, and the real nGrinder agent itself runs on JDK 11.

1. Use JDK 11 (e.g. `~/.jdks/corretto-11.0.32.1/bin/java`), not the host's default JDK.
2. Build the classpath from the controller's own bundled jars (these are the exact versions the
   real agent/worker uses), found at
   `/srv/ngrinder/ngrinder-controller/lib/ngrinder-controller-3.5.9-p1.war-spring-boot-libs-*/`
   with `find <dir> -maxdepth 1`, matching: `grinder-*3.9.1*.jar`, `groovy-*3.0.5*.jar`,
   `ngrinder-*3.5.9-p1*.jar`, `junit-4.13.1.jar`, `hamcrest-*.jar`.
3. Invoke: `<jdk11>/bin/java -cp <classpath> org.codehaus.groovy.tools.FileSystemCompiler -d
   <outdir> -cp <classpath> <script.groovy>`. Exit code 0 and the expected `.class` files confirm
   the script is syntactically and semantically valid against the real runtime's library versions.

---

## 4. Diagnosing a Run From the Host

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
   aggregated view, and cross-check its counts against the worker log's raw call counts
   (Section 2.2) before trusting it as the delivered request count.
