package rainbowclasses

import com.jetbrains.php.lang.psi.elements.PhpClass

/**
 * Hash input for a class/interface/trait/enum.
 *
 * With namespace → "Namespace\ClassName"
 * Without namespace → "filePath::ClassName"
 */
fun PhpClass.fqcnKey(): String {
    val ns = namespaceName.trimEnd('\\')
    return if (ns.isNotEmpty()) {
        "$ns\\$name"
    } else {
        val path = containingFile?.virtualFile?.path ?: containingFile?.name ?: "unknown"
        "$path::$name"
    }
}

/** Anonymous classes (no meaningful name) are excluded from all markers. */
fun PhpClass.isAnonymousClass(): Boolean = isAnonymous || name.isNullOrEmpty()
