package com.hour.onegoal

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hour.onegoal.Data.Category

class MainRvAdapter(val context: Context, val categoryList: ArrayList<Category>, val itemClick: (Category) -> Unit)
    : RecyclerView.Adapter<MainRvAdapter.Holder>() {

    inner class Holder(itemView: View?,itemClick: (Category) -> Unit) : RecyclerView.ViewHolder(itemView!!){
        val categoryPhoto = itemView?.findViewById<ImageView>(R.id.category_image)
        val categoryTitle = itemView?.findViewById<TextView>(R.id.category_title)

        fun bind(category:Category, context: Context){

            categoryPhoto?.setImageResource(category.categoryPhoto)
            /* 나머지 TextView와 String 데이터를 연결한다.*/
            categoryTitle?.text = category.title


            itemView.setOnClickListener {
                if(position == 0){
                    val intent = Intent(context, WorkoutActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }
                if(position == 1){
                    val intent = Intent(context, StudyActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }
                if(position == 2){
                    val intent = Intent(context, MusicActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        // 화면을 최초 로딩하여 만들어진 View 가 없는 경우,
        // xml 파일을 inflate 하여 ViewHolder 를 생성한다.
        val view = LayoutInflater.from(context).inflate(R.layout.main_rv_item,parent,false)
        return Holder(view,itemClick)
    }

    override fun getItemCount(): Int {
        //RecyclerView 로 만들어지는 item 의 총 개수를 반환한다.
        return categoryList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        // 위의 onCreateViewHolder 에서 만든 view 와 실제 입력되는 각각의 데이터를 연결한다.
        holder.bind(categoryList[position],context)
    }

}
