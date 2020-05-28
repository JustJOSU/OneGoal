package com.hour.onegoal

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hour.onegoal.Data.Mission
import com.hour.onegoal.Data.WorkoutRoom
import com.hour.onegoal.Util.loadImage
import de.hdodenhof.circleimageview.CircleImageView
import soup.neumorphism.NeumorphCardView
import soup.neumorphism.NeumorphTextView


class MissionHistoryAdapter(val context: Context, private val historyList: ArrayList<Mission>,
                            var itemClick: (Mission) -> Unit)
    : RecyclerView.Adapter<MissionHistoryAdapter.Holder>() {

    inner class Holder(itemView: View?, itemClick: (Mission) -> Unit) : RecyclerView.ViewHolder(itemView!!){
        val missionUserName = itemView?.findViewById<NeumorphTextView>(R.id.missionUserName)
        val missionPhoto = itemView?.findViewById<ImageView>(R.id.missionPhoto)

        fun bind(mission: Mission, context: Context){
            missionUserName?.text = mission.missionUser
            missionPhoto?.loadImage(mission.missionPhotoUrl)
        }
        /** 만약에 히스토리 아이디 클릭했을 경우 이벤트 발생시키려면 사용
        init {
        itemView?.setOnClickListener {
        val room = workoutList[adapterPosition]
        //TODO: 아이템뷰 클릭했을 때 intent
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
        }
         **/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        // 화면을 최초 로딩하여 만들어진 View 가 없는 경우,
        // xml 파일을 inflate 하여 ViewHolder 를 생성한다.
        val view = LayoutInflater.from(context).inflate(R.layout.mission_history_item,parent,false)
        return Holder(view,itemClick)
    }

    override fun getItemCount(): Int {
        //RecyclerView 로 만들어지는 item 의 총 개수를 반환한다.
        return historyList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        // 위의 onCreateViewHolder 에서 만든 view 와 실제 입력되는 각각의 데이터를 연결한다.
        holder.bind(historyList[position],context)
    }

}
