# Test Method Naming Convention

- **Format:** `testMethodName_givenCondition_willDoAction`
- **Conciseness:** Should not be overly verbose. Use clear and simple language that gets to the heart of the matter.
- **Will-Clause Rules:**
    - **Case 1: No return value (Void):** `..._willProcessAction` (e.g., `willReportAbuse`, `willVerifyRequest`)
    - **Case 2: Return value exists:** `..._willReturnResponse` or `..._willReturnReadModel` (Specify the concrete return type name)
    - **Case 3: Exception occurs:** `..._willThrowException` (Fixed format)

# Test Method Display Name Convention (Exceptionally Allowed to Use Korean Only Within This Sector)

- **Coherence:** Must share the same context with the method name. Should not include any additional information beyond what the method name implies.
- **Interpretation Rules For The Will-Clause on Method Names:**
    - **Case 1:** `..._willProcessAction` -> `활동 수행`
    - **Case 2:** `..._willReturnResponse` -> `응답 반환`, `..._willReturnReadModel` -> `읽기 모델 반환`
    - **Case 3:** `..._willThrowException` -> `예외 반환` (Fixed format)