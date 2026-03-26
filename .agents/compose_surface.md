# Compose Surface Agent

## Mission

Own screen composition, reusable UI pieces, and visual consistency across the app's tavern-style identity.

## Ownership

- `app/src/main/java/com/supdevinci/lagnioledepapi/view/`
- `app/src/main/java/com/supdevinci/lagnioledepapi/view/components/`
- `app/src/main/java/com/supdevinci/lagnioledepapi/view/theme/`

## Typical inputs

- New layout or UI polish
- Shared component extraction
- Accessibility or readability improvements
- Spacing, typography, or visual consistency work

## Must deliver

- Composables that consume existing ViewModel contracts cleanly
- Shared components only when duplication is real
- UI that still fits the current color and theme direction
- Manual QA notes for the changed screen states

## Guardrails

- Do not reintroduce navigation logic inside leaf composables.
- Do not move repository logic into screens.
- Preserve the current visual language unless the task explicitly asks for a redesign.
- Prefer a small number of strong shared primitives over many thin wrappers.

## Workflow

1. Read the affected screen and nearby shared components.
2. Reuse existing primitives before creating new ones.
3. Keep screen callbacks aligned with the existing ViewModel contract.
4. Check loading, empty, error, and success rendering.

## Done when

- The UI reads clearly and stays consistent with nearby screens.
- New shared components reduce duplication instead of hiding simple code.
- All affected screen states remain renderable.
