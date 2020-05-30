package com.hour.onegoal

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.hour.onegoal.Data.WorkoutRoom
import com.hour.onegoal.Util.loadImage
import com.makeramen.roundedimageview.RoundedImageView
import org.w3c.dom.Text
import soup.neumorphism.NeumorphTextView


class WorkOutAdapter(val context: Context, val workoutList: ArrayList<WorkoutRoom>,
                     var itemClick: (WorkoutRoom) -> Unit)
    : RecyclerView.Adapter<WorkOutAdapter.Holder>() {

    inner class Holder(itemView: View?, itemClick: (WorkoutRoom) -> Unit) : RecyclerView.ViewHolder(itemView!!){
        val workoutPhoto = itemView?.findViewById<RoundedImageView>(R.id.item_photo)
        val workoutTitle = itemView?.findViewById<NeumorphTextView>(R.id.item_title)
        val workoutSummary = itemView?.findViewById<TextView>(R.id.item_summary)
        val workoutInformation = itemView?.findViewById<ImageView>(R.id.item_information)
        val workoutCountNumber = itemView?.findViewById<TextView>(R.id.item_countNumber)
        fun bind(workoutRoom: WorkoutRoom, context: Context){
            workoutTitle?.text = workoutRoom.title
            workoutSummary?.text = workoutRoom.summary
            workoutPhoto?.loadImage(workoutRoom.photoUrl)
        }
        init {
            // 정보 아이콘 눌렀을때
            workoutInformation?.setOnClickListener {
                val room = workoutList[adapterPosition]
                val intent = Intent(context,WorkOutRoomActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("roomId",room.roomId)
                intent.putExtra("title",room.title)
                intent.putExtra("summary",room.summary)
                intent.putExtra("description",room.description)
                intent.putExtra("photoUrl",room.photoUrl)
                intent.putExtra("teamHead",room.teamHead)
                context.startActivity(intent)
            }
            // 숫자 아이콘 눌렀을때 현재 참가자 보여줌
           workoutCountNumber?.setOnClickListener {
                val room = workoutList[adapterPosition]
                val intent = Intent(context,ParticipantsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("roomId",room.roomId)
                intent.putExtra("title",room.title)
                intent.putExtra("summary",room.summary)
                intent.putExtra("description",room.description)
                intent.putExtra("photoUrl",room.photoUrl)
                intent.putExtra("teamHead",room.teamHead)
                context.startActivity(intent)
            }
            //TODO : 숫자 1/8 ~ 7/8 까지는 WAITING
            //TODO : 숫자 8/8 일때는 FULL



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        // 화면을 최초 로딩하여 만들어진 View 가 없는 경우,
        // xml 파일을 inflate 하여 ViewHolder 를 생성한다.
        val view = LayoutInflater.from(context).inflate(R.layout.room_rv_item,parent,false)
        return Holder(view,itemClick)
    }

    override fun getItemCount(): Int {
        //RecyclerView 로 만들어지는 item 의 총 개수를 반환한다.
        return workoutList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        // 위의 onCreateViewHolder 에서 만든 view 와 실제 입력되는 각각의 데이터를 연결한다.
        holder.bind(workoutList[position],context)
    }

}
