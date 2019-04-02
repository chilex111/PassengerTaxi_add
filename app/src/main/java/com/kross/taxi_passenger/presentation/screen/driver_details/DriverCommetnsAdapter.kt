package com.kross.taxi_passenger.presentation.screen.driver_details

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.response.ReviewItem
import com.kross.taxi_passenger.presentation.widget.BaseAdapter
import kotlinx.android.synthetic.main.comment_item.view.*
import java.util.*


class DriverCommetnsAdapter(context: Context) : BaseAdapter<ViewHolderDriverCommetns, ReviewItem>() {


    private val listCars = ArrayList<ReviewItem>()
    private val context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDriverCommetns {
        val view = LayoutInflater.from(parent.context).inflate(getLayout(), parent, false)
        return ViewHolderDriverCommetns(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolderDriverCommetns, position: Int) {
        super.onBindViewHolder(holder, position)
        val comment = super.list.get(position)
        holder.bind(comment)


        Glide.with(context)
                .load(comment.photo)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.itemView.userPhotoComm)
        holder.itemView.userNameTvComment.text = comment.firstName + " " + comment.lastName
        holder.itemView.commentDateTv.text = comment.date
        holder.itemView.commentMessageTv.text = comment.text
        holder.itemView.ratingBar.rating = comment.rating!!
    }


    override fun getLayout(): Int {
        return R.layout.comment_item
    }

    fun filter(text: String, list: List<ReviewItem>): Boolean {
        listCars.clear()
        listCars.addAll(list)
        notifyDataSetChanged()
        val result = list.isNotEmpty()
        return result
    }





}