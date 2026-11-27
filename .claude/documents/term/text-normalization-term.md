# Text Normalization Terms

Terms of the text normalization (`infrastructure/normalization/`) and the areas that use it.

---

## 1. Glossary

- **Lightly normalized range** and **original range**: of a code unit of a view's text, the range `[start, end)` of
  code-unit indexes it derives from in the lightly normalized text and in the original text.
- **Original text**: the `String` that `TextNormalizer` builds the views from.
- **Token**: a sequence of consecutive code points of the lightly normalized text that holds no whitespace (ICU4J
  `isUWhiteSpace`) and is bounded on each side by whitespace or the start or end of the text.
- **View**: a `TextNormalizedView` built from the original text, as the `view/` subclass named by a
  `TextNormalizationType` constant: the lightly normalized view (`LIGHT`) from `normalizeLightly`, the aggressively
  normalized view (`AGGRESSIVE`) from `normalizeAggressively`, the syllable normalized view (`SYLLABLE`) from
  `normalizeWithSyllable`, the keystroke normalized view (`KEYSTROKE`) from `normalizeWithKeystroke`, and the
  line-initial normalized view (`LINE_INITIAL`) from `normalizeWithLineInitial`.
- **View's text**: the normalized `String` a view holds; the lightly normalized text is the lightly normalized view's
  text, and likewise for the other views.
