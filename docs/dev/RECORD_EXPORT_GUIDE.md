# Adding custom record export to Boomega

> The Record Export API is unstable and a lot of refactoring needs to be done.
> For this reason, this guide is not finished.

Boomega allows users to export their [Record](/boomega-database/src/main/kotlin/com/dansoftware/boomega/database/api/data/Record.kt)s into
some output formats (like JSON and Excel spreadsheet). The options are expandable by contributing your own record-exporters
to the project or by writing plugins.

### Prerequisites

**These apply to you only if you're contributing to the core project:**

* Make sure you've read the [contribution guideline](/CONTRIBUTING.md)
* Work in the [`boomega-export`](/boomega-export) subproject

### The `RecordExporter` interface

The entities directly responsible for exporting records into a specific format
are [RecordExporter](/boomega-export/src/main/kotlin/com/dansoftware/boomega/export/api/RecordExporter.kt)s.

### Register your RecordExporter

**This section applies to you only if you contribute to the core project.**

After you've created your record-exporter implementation you have to register it's
full [class-name](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Class.html#getName()) in
the [internal_exporters.json](/boomega-export/src/main/resources/com/dansoftware/boomega/export/internal_exporters.json)
config file.

Like this:

```json
[
  ...,
  "com.dansoftware.boomega.export.MyExporter"
]
```

---
See the [plugin guide](../PLUGIN_GUIDE.md#record-exporting-plugins) if you want to write record exporter plugins.