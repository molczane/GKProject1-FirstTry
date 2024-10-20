import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun <T, R> modifyListParallel(list: List<T>, transform: suspend (T) -> R): List<R> {
    return coroutineScope {
        // Używamy mapowania i async, aby przetworzyć każdy element równolegle
        list.map { element ->
            async {
                transform(element) // Równoległe przetwarzanie każdego elementu
            }
        }.awaitAll() // Oczekiwanie na wyniki wszystkich korutyn
    }
}