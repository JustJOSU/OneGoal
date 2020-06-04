package com.hour.onegoal

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
        val workoutState = itemView?.findViewById<TextView>(R.id.item_state)
        val workoutCountNumber = itemView?.findViewById<TextView>(R.id.item_countNumber)

        fun bind(workoutRoom: WorkoutRoom, context: Context){
            val room = workoutList[adapterPosition]
            val members_ref = FirebaseDatabase.getInstance().getReference("workOutRooms/${room.roomId}/members")

            workoutTitle?.text = workoutRoom.title
            //TODO: state

            workoutPhoto?.loadImage(workoutRoom.photoUrl)

            members_ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    // 2020-05-31 22:49 조성재
                    // 방의 입장인원을 보여주는 부분
                    // 현재는 입장에 인원 제한을 두지 않았음으로 향후 업데이트 필요
                    workoutCountNumber?.text = "${p0.childrenCount}/8"
                    if (workoutCountNumber?.text == "8/8"){
                        workoutCountNumber.setTextColor(Color.parseColor("#e8b854"))
                        workoutState?.text = "FULL"
                        itemView.alpha = 0.5F
                    }
                }
            })
        }
        init {
            // 정보 아이콘 눌렀을때
            itemView?.setOnClickListener {
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
