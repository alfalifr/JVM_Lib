package sidev.lib.jvm.tool.`fun`

//====== StringBuffer ==========
operator fun StringBuffer.plusAssign(char: Char) {
    append(char)
}
operator fun StringBuffer.plusAssign(cs: CharSequence) {
    append(cs)
}
operator fun StringBuffer.plusAssign(sb: StringBuffer) {
    append(sb)
}
operator fun StringBuffer.plusAssign(i: Int) {
    append(i)
}
operator fun StringBuffer.plusAssign(l: Long) {
    append(l)
}

//====== StringBuilder ==========
operator fun StringBuilder.plusAssign(char: Char) {
    append(char)
}
operator fun StringBuilder.plusAssign(cs: CharSequence) {
    append(cs)
}
operator fun StringBuilder.plusAssign(sb: StringBuffer) {
    append(sb)
}
operator fun StringBuilder.plusAssign(i: Int) {
    append(i)
}
operator fun StringBuilder.plusAssign(l: Long) {
    append(l)
}