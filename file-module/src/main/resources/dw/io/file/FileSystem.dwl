/**
* Simple File module to do basic operations
*/
%dw 2.0


type Path = String

/**
* The two kind of file
*/
type FileKind = "File" | "Folder"


/**
* Returns the list child file path
*
* === Parameters
*
* [%header, cols="1,3"]
* |===
* | Name   | Description
* | folder | The of the contained file
* |===
*
* === Example
*
* This example shows how the `ls` behaves under different inputs.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
*   ls("/tmp")
*
* ----
*
* ==== Output
*
* [source,Json,linenums]
* ----
*
* ["/tmp/foo.txt","/tmp/dw-input-buffer-0.tmp","/tmp/dw-output-buffer-0.tmp"]
* ----
**/
fun ls(folder: Path): Array<Path> = native("file::LSFunction")

/**
* Return the list of child elements of the specified path. That matches the specified regex pattern
*
* === Parameters
*
* [%header, cols="1,3"]
* |===
* | Name   | Description
* | folder | The of the contained file
* | filterExpr | The file expression regex to be used on each name
* |===
*
* === Example
*
* This example shows how to filter tmp file to all the one that contains the text 'dw'
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
*   ls("/tmp", /dw/)
*
* ----
*
* ==== Output
*
* [source,Json,linenums]
* ----
*
* ["/tmp/dw-input-buffer-0.tmp","/tmp/dw-output-buffer-0.tmp"]
* ----
**/
fun ls(folder: Path, filterExpr: Regex): Array<Path> = do {
    ls(folder) filter ((dir) -> nameOf(dir) matches (filterExpr))
}

/**
* Returns the file type. FileType or DirType
*/
fun fileTypeOf(path: Path): FileKind = native("file::FileTypeOfFunction")

/**
* Returns the name of this file
*/
fun nameOf(path:Path): String = native("file::NameOfFunction")

/**
* Returns the content of the specified file
*/
fun contentOf(path: Path): Binary = do {
    readUrl("file://" ++ path, "binary") as Binary
}

/**
* Returns the infered mimeType of the specified file if not returns null
*/
fun mimeTypeOf(path: Path): String | Null = native("file::MimeTypeOfFunction")

/**
* Returns the base name of this file
*/
fun baseNameOf(path: Path): String = do {
    var name = nameOf(path)
    var lastDotIndex =  (name find ".")[-1] default -1
    ---
    if(lastDotIndex == -1)
        name
    else
        name[0 to (lastDotIndex - 1)]
}

fun extensionOf(path: Path): String | Null = do {
    var lastDotIndex =  (path find ".")[-1] default -1
    ---
    if(lastDotIndex == -1)
        null
    else
        path[lastDotIndex to -1]
}

/**
 * Returns the Path value of the tmp directory.
 **/
fun tmp(): Path = native("file::TmpPathFunction")

/**
* Creates a valid path from this two parts
*/
fun path(basePath: Path, part: String): Path = native("file::PathFunction")
