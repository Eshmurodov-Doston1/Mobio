package uz.idea.mobio.repository.apiServiceRepository.pagingRepository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import uz.idea.mobio.network.pagingSourse.BasePagingSource

private const val DEFAULT_PAGE_SIZE = 10
fun <T : Any> createPager(pageSize: Int = DEFAULT_PAGE_SIZE, enablePlaceholders: Boolean = false,
        block: suspend (Int) -> List<T>): Pager<Int, T> = Pager(
    config = PagingConfig(enablePlaceholders = enablePlaceholders, pageSize = pageSize),
    pagingSourceFactory = { BasePagingSource(block) })
