# Step 11: Inspection Events and Publish Sites

## 1. Purpose

Adds the two inspection events, publishes them from the post and comment write paths, and masks published post titles
and bodies on the post write path. It is separate from the listener because it touches only the post and comment
domains. An event without a listener is a no-op, so this step can merge before step 12.

---

## 2. Scope

| Item | Decision |
|---|---|
| Targets | Published post title and body (create and update); comment content (register and update) |
| Masking | Published post title and body (create and update) are masked |

---

## 3. Structure Design

### 3.1 Component Flow

```
PostController.createPost/updatePost (class-level @Transactional)
 ├→ isPublished → ProhibitedWordHolder.maskProhibitedWord(title), maskProhibitedWord(contentText)
 │ → build the post from the masked text
 ├→ postRepository.save(post) → PostId # PostRepositoryJpaAdapter
 └→ isPublished → PostAutoAbuseInspectionEvent.create(postUlid, raw title, raw contentText, createdAt) → ApplicationEventPublisher
CommentController.register/updateContent (class-level @Transactional)
 ├→ ProhibitedWordHolder.maskProhibitedWord(content) → save the masked content
 └→ CommentAutoAbuseInspectionEvent.create(postUlid, path, raw content, createdAt) → ApplicationEventPublisher
 ↓ after commit
AFTER_COMMIT listeners (none until step 12)
```

### 3.2 Package Structure

Packages are relative to `src/main/java/kr/modusplant/`. Classes marked `existing` are changed; every other class is
new.

- `domains/post/adapter/controller/`
  - `PostController` — existing; masks a published post's title and body and publishes `PostAutoAbuseInspectionEvent`
    after a published create or update
- `domains/post/framework/outbound/jpa/repository/`
  - `PostRepositoryJpaAdapter` — existing; `save` returns `PostId`
- `domains/comment/adapter/controller/`
  - `CommentController` — existing; publishes `CommentAutoAbuseInspectionEvent` after register and update

### 3.3 Implementation-Wide Changes

#### 3.3.1 Claude Document Changes

Every step's Claude document changes follow these rules:
- Paths are relative to `.claude/`.
- `documents/policy/prohibited-word-filtering-policy.md` holds the prohibited-word policy across domains, and states
  every implementation fact of `infrastructure/prohibitedword/` it needs in its own sentences, without naming the rule
  that covers it, because a parent document does not depend on its children.
- The policy uses a term this implementation coins only after its Glossary section defines it, because a coined term
  needs its own definition in every document that uses it.

### 3.4 Step-Specific Changes

#### 3.4.1 Claude Document Changes

**Step 11**:

| Document | Section | Change |
|---|---|---|
| `documents/policy/prohibited-word-filtering-policy.md` | §1 | Post title and body (published create and update) join the table as masked by `PostController`; the closing sentence states that draft post titles and contents are stored as submitted |
| `documents/policy/prohibited-word-filtering-policy.md` | §3.1 | Register and content update publish `CommentAutoAbuseInspectionEvent` with the raw content |
| `documents/policy/prohibited-word-filtering-policy.md` | New §3.5 Post (`PostController`), before Stored Nicknames, which becomes §3.6 | Published create and update mask the title and body with `maskProhibitedWord` before the save and publish `PostAutoAbuseInspectionEvent` with the raw title and body after the save; drafts are not masked and publish nothing |

---

## 4. Technical Details

### 4.1 Events

- `PostAutoAbuseInspectionEvent` carries `postUlid`, `title`, `contentText`, and `createdAt`, and
  `CommentAutoAbuseInspectionEvent` carries `postUlid`, `path`, the raw content, and `createdAt`.
- The text fields of the events may be blank (a title-only post), so their `create(...)` validates only identifiers and
  the creation time.
- Title and body travel as separate fields, so step 12 scans them separately.
- The creation time is taken at the publish site, because handlers run asynchronously and can finish out of order, so
  step 12 dates each detection by its write, not by its handling.
- The post domain has no `domain/event/` package yet; it is created with `PostAutoAbuseInspectionEvent`.

### 4.2 Post Publish Sites

| Site | Change |
|---|---|
| `PostController.createPost` | When `isPublished`, mask `title` and `contentText` of `PostRequest` with `maskProhibitedWord` before building the post, and publish `PostAutoAbuseInspectionEvent` with the raw `title` and `contentText` after `postRepository.save` |
| `PostController.updatePost` | Same, when `isPublished` |

- Drafts are neither masked nor inspected, because they are visible only to their author. A published post cannot
  return to draft, so an inspected post never leaves the inspected scope.
- The event carries the raw text, because a masked text can no longer be scored.
- `Post.createPublished` leaves `postId` null, and `PostEntity.ulid` is generated by `@UlidGenerator` on persist, while
  `PostRepository.save` returns `void`. `save` therefore returns the saved `PostId`, because `createPost` has no other
  way to name the post it publishes, and the universally unique lexicographically sortable identifier (ULID) is known
  only after the persist.
- `PostController` injects neither `ApplicationEventPublisher` nor `ProhibitedWordHolder` yet, so it gains both.

### 4.3 Comment Publish Sites

- `CommentController` already injects `ApplicationEventPublisher`; register publishes `CommentAutoAbuseInspectionEvent`
  next to `CommentRegisterEvent`.
- The event carries the raw `content` of `CommentRegisterRequest` and `CommentUpdateRequest`.
- The idempotent early return of register publishes nothing.

---

## 5. Other Implementation Requirements

- When the step is done, published post titles and bodies are stored masked, both events are published from their
  sites, and no listener handles them yet.
- Every step's result must build and pass its tests.

---

## 6. Glossary

- **Prohibited word**: A word that is masked, rejected in a nickname, and scored, stored in `prohibited_word`
- **Published post, draft**: A post visible to every member, or a post visible only to its author
  (`isPublished = false`)
- **Raw content**: The text as the request carries it, before `maskProhibitedWord` masks it
- **Inspection event**: `PostAutoAbuseInspectionEvent` or `CommentAutoAbuseInspectionEvent`, which hands a written text
  to detection
- **Auto-report**: A report that the detector writes for admin review while the latest write of its target is
  reportable, stored apart from human abuse reports and never counted in them
