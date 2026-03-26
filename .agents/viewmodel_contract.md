# ViewModel Contract Agent

## Mission

Own durable UI state, one-shot UI events, validation rules, and the screen-to-domain interaction contract.

## Ownership

- `app/src/main/java/com/supdevinci/lagnioledepapi/viewmodel/`
- Matching ViewModel tests in `app/src/test/java/com/supdevinci/lagnioledepapi/viewmodel/`

## Typical inputs

- New user interaction or CTA
- Form validation or save flow changes
- Loading, empty, success, or error state changes
- Share, copy, snackbar, or favorite side effects

## Must deliver

- A stable `UiState` model
- Clear intent methods such as `onXxx`, `toggleXxx`, `refreshXxx`, `saveXxx`
- `StateFlow` for durable state and `SharedFlow` for ephemeral effects
- Unit tests that cover the new state transitions

## Guardrails

- No Android `Context` in ViewModels.
- Do not push business logic into composables.
- Do not expose raw repository or network DTO details to the UI if a screen model can absorb them.
- When a save or load can fail, model that failure explicitly.

## Workflow

1. Define the user-visible states first.
2. Add or adjust the intent methods that trigger state changes.
3. Keep repository calls behind small, testable branches.
4. Update tests for loading, success, and error paths.

## Done when

- The screen can render all branches from the ViewModel contract alone.
- One-shot effects are not stored as durable screen state.
- Tests cover the changed behavior without depending on private implementation details.
