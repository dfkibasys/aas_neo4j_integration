---

# Neo4j Cypher Script for AAS Submodel Element Creation/Update

This Cypher script is designed to **create or update `SubmodelElement` nodes** and their hierarchical (`HAS_ELEMENT`) and reference (`HAS_REFERENCE`, `HAS_SEMANTIC`) relationships within a Neo4j graph database. It's built for handling the dynamic nature of AAS Submodel Elements, including their properties, labels, and various types of references.

The script leverages a Pebble templating approach to generate a series of `MERGE` statements, ensuring idempotency and efficient graph updates based on a provided list of `SubmodelElement` objects.

---

## 1. Core Logic & Functionality

The primary purpose of this script is to:

* **Idempotently Create/Update Submodel Elements:** Uses `MERGE` on `(e:SubmodelElement {smId: ..., idShortPath: ...})` to create a node if it doesn't exist or match it if it does, based on its unique identifiers within a Submodel.
* **Set Properties:** Dynamically sets `idShort`, `sourceUrl`, and `value` properties on the `SubmodelElement` node `e` if they are present in the input data.
* **Apply Labels:** Dynamically applies additional labels to `e` based on the `elem.labels` list.
* **Establish Hierarchy:** Connects the `SubmodelElement` (`e`) to its parent (`p`) via a `HAS_ELEMENT` relationship. The parent can either be the root `Submodel` node itself (if `e` is a root element within the Submodel) or another `SubmodelElement`. This relationship is also `MERGE`d for idempotency.
* **Create References:** For each reference specified in `elem.refs`:
    * It `MERGE`s the target of the reference (`re_X`) using its `id` and optionally `idShortPath` and `label`.
    * It `MERGE`s a relationship from the `SubmodelElement` (`e`) to its target (`re_X`). The relationship type is either `HAS_SEMANTIC` (if `eachRef.semantics` is true) or `HAS_REFERENCE`, with an additional `type` property from `eachRef.refType`.

---

## 2. Dynamic Script Generation (Pebble Templating)

This script is an example of how a single template can generate multiple Cypher `statement` blocks for each `SubmodelElement` (`elem`) in the `smeList`.

* **`smeList = collectAllSubmodelElements(sme, smId, smePath)`**: This line (executed outside the `for` loop, likely in the preceding logic) is crucial. It suggests a custom function or helper that collects all relevant `SubmodelElement` data, potentially including their hierarchical parents and all associated references, into a list (`smeList`). This list then drives the `{%- for elem in smeList %}` loop.
* **Looping (`{%- for elem in smeList %}`):** The template iterates over each `SubmodelElement` (`elem`) in the `smeList`, generating a complete Cypher statement for each one.
* **Conditional Property/Label Setting (`{%- if ... %}`):** Properties and labels are only applied if their corresponding values are not null or empty in the `elem` object.
* **Dynamic Parent (`{%- if elem.isRootElement %}`):** The script intelligently determines if `e`'s parent `p` is the `Submodel` node or another `SubmodelElement` based on the `isRootElement` flag.
* **Dynamic References (`{% for eachRef in elem.refs %}`):** For each reference (`eachRef`) associated with `elem`, a new target node `re_X` and a corresponding `HAS_REFERENCE` or `HAS_SEMANTIC` relationship are `MERGE`d.

---

## 3. Parameters

The generated Cypher statements will use parameters dynamically derived from the `event` object and the `elem` iterated in the loop.

* **`submodel.id`**: The `id` of the `Submodel` the element belongs to, taken from `event.id`.
* **`parent.idShortPath`**: (Conditional) The `idShortPath` of `elem`'s parent `SubmodelElement`, if `elem` is not a root element.
* **`sme.idShortPath`**: The `idShortPath` of the current `SubmodelElement` (`elem`).
* **`sme.idShort`**: (Conditional) The `idShort` property of the `SubmodelElement`.
* **`sme.sourceUrl`**: (Conditional) The `sourceUrl` property for the `SubmodelElement`, dynamically constructed using URL encoding. Note the `meta.sourceUrl` reference here suggests a higher-level `meta` object might be involved in the templating context.
* **`sme.value`**: (Conditional) The `value` property of the `SubmodelElement`.
* **`refs.r_X.id`**: The `id` of a referenced target.
* **`refs.r_X.idShortPath`**: (Conditional) The `idShortPath` of a referenced target.
* **`refs.r_X.label`**: (Conditional) An optional label for a referenced target.
* **`refs.r_X.type`**: The type of the reference relationship (e.g., "global", "modelReference").

---