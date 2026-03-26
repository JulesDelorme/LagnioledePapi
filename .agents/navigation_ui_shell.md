# Navigation UI Shell Agent

## Mission

Own navigation, route contracts, back stack behavior, and screen wiring. Keep the shell simple and predictable.

## Ownership

- `app/src/main/java/com/supdevinci/lagnioledepapi/MainActivity.kt`
- `app/src/main/java/com/supdevinci/lagnioledepapi/viewmodel/AppViewModelProvider.kt`
- `app/src/main/java/com/supdevinci/lagnioledepapi/model/Models.kt` only when route arguments or `CocktailSource` handling must evolve

## Typical inputs

- Add a new top-level or detail screen
- Change a user flow or back navigation rule
- Fix bottom bar visibility or tab restoration
- Add route arguments or deep-link style entry points

## Must deliver

- A route contract with argument names and types
- Correct `navigate`, `popUpTo`, `launchSingleTop`, and `restoreState` behavior
- Screen to ViewModel wiring that matches the current factory pattern
- A short impact check for bottom bar visibility and local/remote detail flows

## Guardrails

- Do not move business logic into navigation code.
- Do not edit repositories unless the task is explicitly a vertical slice and no other agent owns that change.
- Preserve the current `CocktailSource.LOCAL` and `CocktailSource.REMOTE` split unless the task explicitly changes that contract.
- Avoid architecture churn such as introducing Hilt or a new nav module during routine work.

## Workflow

1. Read the current routes in `MainActivity.kt`.
2. Confirm how arguments are built and parsed.
3. Update screen wiring and factory calls only where needed.
4. Check whether the change affects bottom bar visibility or back behavior.

## Done when

- Every affected destination is reachable.
- Arguments are symmetric between route builder and parser.
- Back navigation and tab restoration still make sense after the change.
