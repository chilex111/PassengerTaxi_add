package com.kross.taxi_passenger.presentation.screen.cabinet.news.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.response.NewsItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_news.*
import kotlinx.android.synthetic.main.item_news.view.*

// TODO: replace String with News object

class NewsViewHolder(override val containerView: View?,
                     private val share: (NewsItem) -> Unit,
                     private val more: (NewsItem) -> Unit) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    private lateinit var globalData: NewsItem

    init {
        shareButton.setOnClickListener {
            share(globalData)
        }
        readMoreButton.setOnClickListener {
            more(globalData)
        }
    }

    fun configure(data: NewsItem) {
        globalData = data
    }

}

class NewsAdapter(private val shareListener: (NewsItem) -> Unit,
                  private val moreListener: (NewsItem) -> Unit,
                  val context: Context) : RecyclerView.Adapter<NewsViewHolder>() {

    private var items: MutableList<NewsItem> = ArrayList()
        set(value) {
            if (value != items) {
                field = value
                notifyDataSetChanged()
            }
        }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.configure(items[position])
        holder.itemView.titleTextView.text = items[position].title
        holder.itemView.descriptionTextView.text = items[position].text
        holder.itemView.pubDateTv.text = items[position].published_date
        if (items[position].image != null) {
            Glide.with(context)
                    .asBitmap()
                    .load(items[position].image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.itemView.imageView)
        } else{
            holder.itemView.imageView.visibility = View.GONE
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_news, parent, false)
                .let {
                    return NewsViewHolder(it, shareListener, moreListener)
                }
    }

    fun addObjects(objects: List<NewsItem>) {
        items.addAll(objects)
        notifyDataSetChanged()
    }

}