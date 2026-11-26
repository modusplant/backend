# Step 11: Inspected Events and Publish Sites

Expands `prohibited-word-auto-report-implementation.md`.

## 1. Purpose

Adds the inspected events, publishes them from the post and comment write paths, and masks post titles and
bodies on the post write path. It is separate from the listener because, apart from the event classes, it touches only
the post and comment domains. An event without a listener is a no-op, so this step can merge before step 12.

---

## 2. Scope

- **Inspection targets**: `PostController.createPost` and `updatePost` publish a post event only when `isPublished`
  (§4.2), and `CommentController.register` and `updateContent` publish a comment event (§4.3); a write with matches
  and an update without matches both publish an inspected event, and a create or register without matches publishes
  nothing (§4.1).
- **Masking**: `PostController.createPost` and `updatePost` mask `title` and `contentText` of every `PostRequest`,
  published or draft, with `inspectProhibitedWord` before building the post (§4.2).

---

## 3. Structure Design

### 3.1 Component Flow

```
PostController.createPost/updatePost (method-level @Transactional)
 ├→ ProhibitedWordHolder.inspectProhibitedWord(title), inspectProhibitedWord(contentText)
 │ → build the post from the masked texts; pool the matches of both
 ├→ postRepository.save(post) → PostId # PostRepositoryJpaAdapter
 └→ publish decision by isPublished and the pooled matches
  ├[isPublished, with matches or by updatePost]→ PostProhibitedWordMatchInspectedEvent.create(postUlid, matches,
  │ createdAt) → ApplicationEventPublisher
  └[draft, or createPost without matches]→ stop
CommentController.register/updateContent (class-level @Transactional)
 ├→ ProhibitedWordHolder.inspectProhibitedWord(content) → save the masked content
 └→ publish decision by the matches
  ├[matches, or updateContent]→ CommentProhibitedWordMatchInspectedEvent.create(postUlid, path, matches, createdAt)
  │ → ApplicationEventPublisher
  └[register without matches]→ stop
 ↓ after commit
AFTER_COMMIT listeners (none until step 12)
```

### 3.3 Step-Specific Changes

#### 3.3.1 Claude Document Changes

| Document | Section | Change |
|---|---|---|
| `documents/policy/prohibited-word-filtering-policy.md` | §1 | Post title and body (published and draft create and update) join the table as masked by `PostController`, and are removed from the closing sentence's inputs stored as submitted |
| `documents/policy/prohibited-word-filtering-policy.md` | Header | `inspectProhibitedWord`, which masks as `maskProhibitedWord` does and also returns the matches, joins `maskProhibitedWord` and `NicknameValidator` as an entry point |
| `documents/policy/prohibited-word-filtering-policy.md` | §3.1 | Register and content update mask the content with `inspectProhibitedWord`; a write with matches and a content update without matches publish `CommentProhibitedWordMatchInspectedEvent` with the matches, possibly none, and a register without matches publishes nothing |
| `documents/policy/prohibited-word-filtering-policy.md` | New §3.5 Post (`PostController`), before Stored Nicknames, which becomes §3.6 | Create and update mask the title and body with `inspectProhibitedWord` before the save; after the save, a published create with matches and every published update publish `PostProhibitedWordMatchInspectedEvent` with the pooled matches, possibly none, while a published create without matches and drafts publish nothing |

---

## 4. Technical Details

### 4.1 Events

| Event | Fields | Published by |
|---|---|---|
| `PostProhibitedWordMatchInspectedEvent` | `postUlid`, `matches`, `createdAt` | A published create whose title and body have one or more matches, and every published update |
| `CommentProhibitedWordMatchInspectedEvent` | `postUlid`, `path`, `matches`, `createdAt` | A register whose content has one or more matches, and every content update |

- The events sit in `infrastructure/prohibitedword/event/` rather than in a domain's `domain/event/`, because they
  carry `ProhibitedWordMatch`, and the domain layer depends on no infrastructure class.
- `matches` is the `List<ProhibitedWordMatch>` that `inspectProhibitedWord` returns; for a post it pools the title's and
  the body's matches. `create(...)` accepts an empty list, which an update without matches carries, and validates the
  identifiers and the creation time.
- The creation time is taken at the publish site, because handlers run asynchronously and can finish out of order, so
  step 12 dates each detection by its write, not by its handling.

**From step 09** (§4.3): `inspectProhibitedWord` returns the masked text and every match of one text, and the pooled
matches of a post's title and body share the per-word cap.

### 4.2 Post Publish Sites

| Site | Change |
|---|---|
| `PostController.createPost` | Mask `title` and `contentText` of `PostRequest` with `inspectProhibitedWord` before building the post, and, when `isPublished` and the pooled matches are not empty, publish `PostProhibitedWordMatchInspectedEvent` after `postRepository.save` |
| `PostController.updatePost` | Same, except that the event is published whenever `isPublished`, even with empty pooled matches |

- Drafts are masked but not inspected, because they are visible only to their author. A published post cannot return
  to draft, so an inspected post never leaves the inspected scope.
- `Post.createPublished` leaves `postId` null, and `PostEntity.ulid` is generated by `@UlidGenerator` on persist, while
  `PostRepository.save` returns `void`. `save` therefore returns the saved `PostId`, because `createPost` has no other
  way to name the post it publishes, and the universally unique lexicographically sortable identifier (ULID) is
  known only after the persist.
- `PostController` injects neither `ApplicationEventPublisher` nor `ProhibitedWordHolder` yet, so it gains both.

### 4.3 Comment Publish Sites

- `CommentController` already injects `ApplicationEventPublisher`, and its two `maskProhibitedWord` calls become
  `inspectProhibitedWord` calls. Register publishes `CommentProhibitedWordMatchInspectedEvent` next to
  `CommentRegisterEvent` only with matches, and `updateContent` publishes it after saving the masked content, with
  matches or without.
- The idempotent early return of register publishes nothing.

---

## 5. Other Implementation Requirements

- When the step is done, post titles and bodies are stored masked, the two events are published from their sites,
  and no listener handles them yet.
