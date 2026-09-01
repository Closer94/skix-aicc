import fs from "node:fs/promises";
import { FileBlob, SpreadsheetFile } from "@oai/artifact-tool";

const inputPath = "C:/Users/mp2607041/Desktop/[SK 인텔릭스] 공유폴더/(고객제공자료)/서비스센터_많이하는질문_데이터 추출_20260723.xlsx";
const outputPath = "C:/Users/mp2607041/.codex/visualizations/2026/08/25/01a03720-0574-7991-8ec9-fb02819c8edc/faq-inspection/rows-903-910.png";
const workbook = await SpreadsheetFile.importXlsx(await FileBlob.load(inputPath));
const inspected = await workbook.inspect({
  kind: "table,computedStyle",
  sheetId: "Sheet1",
  range: "A903:G910",
  include: "values,formulas",
  maxChars: 12000,
  tableMaxRows: 8,
  tableMaxCols: 7,
  tableMaxCellChars: 500,
});
console.log(inspected.ndjson);
const preview = await workbook.render({ sheetName: "Sheet1", range: "A903:G910", scale: 2, format: "png" });
await fs.writeFile(outputPath, new Uint8Array(await preview.arrayBuffer()));
