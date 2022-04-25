#### _dw::io::http::BodyUtils_
__________________________________________



# Index

### Functions
| Name | Description|
|------|------------|
| [formatHeader](#formatheader-index ) | |
| [generateBody](#generatebody-index ) | |
| [normalizeHeaders](#normalizeheaders-index ) | Helper method of `normalizeHeaders` to work with Null|
| [safeRead](#saferead-index ) | |









__________________________________________


# Functions

## **formatHeader** [↑↑](#index )

### _formatHeader(header: String): String_


__________________________________________


## **generateBody** [↑↑](#index )

### _generateBody(config: { body?: Any, headers?: Types::HttpHeaders, writerOptions?: Dictionary<Any> }): { body?: Binary, headers: Types::HttpHeaders }_


__________________________________________


## **normalizeHeaders** [↑↑](#index )

### _normalizeHeaders(headers: Null): { _?: String }_

Helper method of `normalizeHeaders` to work with Null
__________________________________________

### _normalizeHeaders(headers: { _*?: SimpleType | Null }): { _?: String }_

Normalize the object to be compliant with the http header

##### Parameters

| Name   | Description|
|--------|------------|
| headers | The headers to normalize|

__________________________________________


## **safeRead** [↑↑](#index )

### _safeRead(mime: String, payload: String | Binary | Null, readerOptions: Object): Any_


__________________________________________






