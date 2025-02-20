package com.ssafy.firstproject.data.repository

import com.ssafy.firstproject.data.model.request.KeywordSearch
import com.ssafy.firstproject.data.model.response.NewsAnalysisArticle
import com.ssafy.firstproject.data.source.remote.ContentSearchService
import com.ssafy.firstproject.data.source.remote.RetrofitCllient.getRetrofitClient
import com.ssafy.firstproject.data.source.remote.RetrofitUtil
import retrofit2.Response

class ContentSearchRepository {

    private val contentSearchService = RetrofitUtil.contentSearchService

    suspend fun searchKeywords(
        keywordSearch: KeywordSearch,
        readTimeout: Long
    ): Response<List<NewsAnalysisArticle>> {
        return if (readTimeout == 10L) {
            contentSearchService.searchKeywords(keywordSearch)
        } else {
            getRetrofitClient(readTimeout).create(ContentSearchService::class.java)
                .searchKeywords(keywordSearch)
        }
    }

    suspend fun searchByUrl(url: String, readTimeout: Long): Response<NewsAnalysisArticle> {
        return if (readTimeout == 10L) {
            contentSearchService.searchByUrl(url)
        } else {
            getRetrofitClient(readTimeout).create(ContentSearchService::class.java)
                .searchByUrl(url)
        }
    }
}