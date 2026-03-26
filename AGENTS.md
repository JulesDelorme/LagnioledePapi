# Agent Playbook

This repo works best with a small set of specialist agents. Do not create one agent per screen. Pick the narrowest agent that can finish the task, keep ownership zones clear, and sequence cross-layer work instead of letting multiple agents edit the same files.

## Core rules

- One agent owns one file zone at a time.
- If a task spans data, state, and UI, work in this order: data contract -> ViewModel contract -> Compose surface -> regression tests.
- UI agents do not change repository behavior unless the task is explicitly a full-stack slice.
- Data agents do not redesign Compose screens.
- Any Room schema change must include schema export, migration, and a migration test.
- Any repository behavior change must include at least one success-path test and one failure-path test.
- Do not let network DTOs leak upward when a stable domain model can be returned instead.
- You are not alone in the repo. Never revert unrelated edits and do not overwrite work owned by another active agent.

## Project map

- Navigation shell: `app/src/main/java/com/supdevinci/lagnioledepapi/MainActivity.kt`
- ViewModel factories: `app/src/main/java/com/supdevinci/lagnioledepapi/viewmodel/AppViewModelProvider.kt`
- Screens and shared UI: `app/src/main/java/com/supdevinci/lagnioledepapi/view/` and `app/src/main/java/com/supdevinci/lagnioledepapi/view/components/`
- Theme: `app/src/main/java/com/supdevinci/lagnioledepapi/view/theme/`
- ViewModels and UI contracts: `app/src/main/java/com/supdevinci/lagnioledepapi/viewmodel/`
- Local persistence: `app/src/main/java/com/supdevinci/lagnioledepapi/data/local/`
- Remote API: `app/src/main/java/com/supdevinci/lagnioledepapi/service/`
- Repositories: `app/src/main/java/com/supdevinci/lagnioledepapi/repository/`
- Models and mapping: `app/src/main/java/com/supdevinci/lagnioledepapi/model/`
- Tests: `app/src/test/java/com/supdevinci/lagnioledepapi/`
- Room schemas: `app/schemas/`

## Agents

- `navigation-ui-shell`: see `.agents/navigation_ui_shell.md`
- `viewmodel-contract`: see `.agents/viewmodel_contract.md`
- `compose-surface`: see `.agents/compose_surface.md`
- `room-integrity-guardian`: see `.agents/room_integrity_guardian.md`
- `repository-regression`: see `.agents/repository_regression.md`
- `api-mapping-contract`: see `.agents/api_mapping_contract.md`

## Recommended default set

For everyday product work, start with:

1. `viewmodel-contract`
2. `navigation-ui-shell` or `compose-surface`
3. `repository-regression`

Add these when the task justifies them:

- `room-integrity-guardian` for DAO, entity, converter, migration, or schema changes
- `api-mapping-contract` for remote API changes or when DTO leakage appears

## Parallel-safe pairs

- `navigation-ui-shell` + `room-integrity-guardian`
- `compose-surface` + `api-mapping-contract`
- `viewmodel-contract` + `repository-regression` when the test agent stays inside test files

## Handoff contracts

- Data agents hand off stable repository signatures, mapping rules, and failure modes.
- `viewmodel-contract` hands off callback signatures and state/event contracts to UI agents.
- `compose-surface` should not invent new repository calls; it consumes the existing ViewModel contract.
- `repository-regression` validates observable behavior, not private implementation details.
