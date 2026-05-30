---
name: testing-member-domain-rest-controller
description: This file provides strict guidance on creating, managing, and refactoring tests for the REST controller within the member domain.
disable-model-invocation: true
---

# Test Architecture

- **Test Convention:**: Covers unit tests that verify method calls, return values, and exceptions by injecting mock dependency into the controller instance, without using MockMvc.