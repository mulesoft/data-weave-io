/**
* Simple File module to do basic operations
*/
%dw 2.0

import dataFormatsDescriptor from dw::Runtime

type Path = String

/**
* Mapping between file extension and MimeType
*/
var FILE_EXTENSIONS: {_: String} = {
    ".txt": "text/plain",
    ".css": "text/css",
    ".html": "text/html",
    ".htm": "text/html",
    ".gif": "image/gif",
    ".jpg": "image/jpeg",
    ".jpe": "image/jpeg",
    ".jpeg": "image/jpeg",
    ".bmp": "image/bmp",
    ".js": "application/javascript",
    ".png": "image/png",
    ".java": "text/plain",
    ".body": "text/html",
    ".rtx": "text/richtext",
    ".tsv": "text/tab-separated-values",
    ".etx": "text/x-setext",
    ".json": "application/json",
    ".class": "application/java",
    ".csh": "application/x-csh",
    ".sh": "application/x-sh",
    ".tcl": "application/x-tcl",
    ".tex": "application/x-tex",
    ".texinfo": "application/x-texinfo",
    ".texi": "application/x-texinfo",
    ".t": "application/x-troff",
    ".tr": "application/x-troff",
    ".roff": "application/x-troff",
    ".man": "application/x-troff-man",
    ".me": "application/x-troff-me",
    ".ms": "application/x-wais-source",
    ".src": "application/x-wais-source",
    ".zip": "application/zip",
    ".bcpio": "application/x-bcpio",
    ".cpio" : "application/x-cpio",
    ".gtar" : "application/x-gtar",
    ".shar" : "application/x-shar",
    ".sv4cpio" : "application/x-sv4cpio",
    ".sv4crc" : "application/x-sv4crc",
    ".tar" : "application/x-tar",
    ".ustar" : "application/x-ustar",
    ".dvi" : "application/x-dvi",
    ".hdf" : "application/x-hdf",
    ".latex" : "application/x-latex",
    ".bin" : "application/octet-stream",
    ".oda" : "application/oda",
    ".pdf" : "application/pdf",
    ".ps" : "application/postscript",
    ".eps" : "application/postscript",
    ".ai" : "application/postscript",
    ".rtf" : "application/rtf",
    ".nc" : "application/x-netcdf",
    ".cdf" : "application/x-netcdf",
    ".cer" : "application/x-x509-ca-cert",
    ".exe" : "application/octet-stream",
    ".gz" : "application/x-gzip",
    ".Z" : "application/x-compress",
    ".z" : "application/x-compress",
    ".hqx" : "application/mac-binhex40",
    ".mif" : "application/x-mif",
    ".ico" : "image/x-icon",
    ".ief" : "image/ief",
    ".tiff" : "image/tiff",
    ".tif" : "image/tiff",
    ".ras" : "image/x-cmu-raster",
    ".pnm" : "image/x-portable-anymap",
    ".pbm" : "image/x-portable-bitmap",
    ".pgm" : "image/x-portable-graymap",
    ".ppm" : "image/x-portable-pixmap",
    ".rgb" : "image/x-rgb",
    ".xbm" : "image/x-xbitmap",
    ".xpm" : "image/x-xpixmap",
    ".xwd" : "image/x-xwindowdump",
    ".au" : "audio/basic",
    ".snd" : "audio/basic",
    ".aif" : "audio/x-aiff",
    ".aiff" : "audio/x-aiff",
    ".aifc" : "audio/x-aiff",
    ".wav" : "audio/x-wav",
    ".mp3" : "audio/mpeg",
    ".mpeg" : "video/mpeg",
    ".mpg" : "video/mpeg",
    ".mpe" : "video/mpeg",
    ".qt" : "video/quicktime",
    ".mov" : "video/quicktime",
    ".avi" : "video/x-msvideo",
    ".movie" : "video/x-sgi-movie",
    ".avx" : "video/x-rad-screenplay",
    ".wrl" : "x-world/x-vrml",
    ".mpv2" : "video/mpeg2",
    ".jnlp" : "application/x-java-jnlp-file",

    ".eot" : "application/vnd.ms-fontobject",
    ".woff" : "application/font-woff",
    ".woff2" : "application/font-woff2",
    ".ttf" : "application/x-font-ttf",
    ".otf" : "application/x-font-opentype",
    ".sfnt" : "application/font-sfnt",

    /* Add XML related MIMEs */

    ".xml" : "application/xml",
    ".xhtml" : "application/xhtml+xml",
    ".xsl" : "application/xml",
    ".svg" : "image/svg+xml",
    ".svgz" : "image/svg+xml",
    ".wbmp" : "image/vnd.wap.wbmp",
    ".wml" : "text/vnd.wap.wml",
    ".wmlc" : "application/vnd.wap.wmlc",
    ".wmls" : "text/vnd.wap.wmlscript",
    ".wmlscriptc" : "application/vnd.wap.wmlscriptc"
}

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
* Returns the file type. File or Folder or null if it doesn't exits
*/
fun kindOf(path: Path): FileKind | Null = native("file::FileTypeOfFunction")

/**
* Returns the name of this file
*/
fun nameOf(path: Path): String = native("file::NameOfFunction")

fun exists(path: Path):Boolean = kindOf(path) != null

/**
* Returns the content of the specified file
*/
fun contentOf(path: Path): Binary = do {
    readUrl(toUrl(path), "binary") as Binary
}


/**
* Returns the content of the specified file
*/
fun toUrl(path: Path): String = native("file::ToUrlFunction")

fun writeTo(path: Path, binary: Binary): Number = native("file::WriteFunction")

fun mkdir(path: Path): Path = native("file::MakeDirFunction")

/**
* Zips all the specified files into the `zipPath`
**/
fun zip(paths: Array<Path>, zipPath:Path): Path = native("file::ZipFunction")

/**
* Unzip the given `zipPath` into the specified directory.
**/
fun unzip(zipPath: Path, zipDirectory:Path): Path = native("file::UnzipFunction")

/**
* Tries to guess the mimeType of the given Path
*
* === Parameters
*
* [%header, cols="1,3"]
* |===
* | Name   | Description
* | path | The path
* |===
*
* === Example
*
* This example shows how the `mimeTypeOf` behaves under different inputs.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
*
*
* ----
*
* ==== Output
*
* [source,Json,linenums]
* ----
*
* ----
**/
fun mimeTypeOf(path: Path): String | Null = do {
    var maybeExtension = extensionOf(path)
    var matchingByExtension = (dataFormatsDescriptor() filter ((item, index) -> item.extensions contains maybeExtension))
    ---
    maybeExtension match {
        case extension is String -> do {
            matchingByExtension[0].defaultMimeType default FILE_EXTENSIONS[extension]
        }
        case is Null -> null
    }
}

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

/**
* Returns the extension of the file with the dot.
*
* === Parameters
*
* [%header, cols="1,3"]
* |===
* | Name   | Description
* | path | The path
* |===
*
* === Example
*
* This example shows how the `extensionOf` behaves under different inputs.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* %dw 2.0
*  import * from dw::io::file::FileSystem
*  output application/json
*  ---
*  {
*    a: extensionOf(path("/tmp","foo.txt")),
*    b: extensionOf(path("/tmp","foo.html")),
*    c: extensionOf(path("/tmp","foo.json")),
*    d: extensionOf(tmp()) //Directory should return null
*  }
*
* ----
*
* ==== Output
*
* [source,Json,linenums]
* ----
* {
*    "a": ".txt",
*    "b": ".html",
*    "c": ".json",
*    "d": null
*  }
* ----
**/
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
 * Returns the Path value of the home directory.
 **/
fun home(): Path = native("file::HomePathFunction")

/**
 * Returns the Path value of the working directory.
 **/
fun wd(): Path = native("file::WorkingDirectoryPathFunction")

/**
* Creates a valid path from this two parts
*/
fun path(basePath: Path, part: String): Path = native("file::PathFunction")




/**
* Creates a valid path from this two parts
*/
fun path(basePath: Path, part: String, part2: String): Path =
    path(path(basePath, part), part2)

fun path(basePath: Path, part: String, part2: String, part3: String): Path =
    path(path(basePath, part), part2, part3)


fun path(basePath: Path, parts: Array<String>): Path =
    parts reduce ((part, accumulator = basePath) -> path(accumulator, part))