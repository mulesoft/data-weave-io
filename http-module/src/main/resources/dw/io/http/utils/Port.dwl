%dw 2.0

/**
* Returns a free port that can be used for binding
*/
fun freePort():Number = native("IO::FreePortFunction")