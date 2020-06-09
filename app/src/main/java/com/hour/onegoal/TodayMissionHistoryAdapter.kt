package com.hour.onegoal

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.hour.onegoal.Data.Mission
import com.hour.onegoal.Data.TodayMission
import com.hour.onegoal.Data.WorkoutRoom
import com.hour.onegoal.Util.loadImage
import com.makeramen.roundedimageview.RoundedImageView
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text
import soup.neumorphism.NeumorphCardView
import soup.neumorphism.NeumorphTextView


class TodayMissionHistoryAdapter(val context: Context, private val todayMissionHistoryList: ArrayList<TodayMission>,
                            var itemClick: (TodayMission) -> Unit)
    : RecyclerView.Adapter<TodayMissionHistoryAdapter.Holder>() {

    inner class Holder(itemView: View?, itemClick: (TodayMission) -> Unit) : RecyclerView.ViewHolder(itemView!!){

        val todayMissiontitle = itemView?.findViewById<TextView>(R.id.item_todayMissionTitle_TextView)
        val todayMissionDate = itemView?.findViewById<TextView>(R.id.item_todayMissionDate_TextView)
        val todayMissionPhotoUrl = itemView?.findViewById<CircleImageView>(R.id.item_todayMissionUserPhotoUrl)
        fun bind(todayMission: TodayMission, context: Context){
            todayMissiontitle?.text = todayMission.todaymissionTitle
            todayMissionDate?.text =  todayMission.todaymissionDate

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
        val view : View?
        return when (viewType){
            TodayMission.DATE_TYPE -> {
                view = LayoutInflater.from(context).inflate(R.layout.today_mission_item,parent,false)
                return Holder(view,itemClick)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }

    }

    override fun getItemCount(): Int {
        //RecyclerView 로 만들어지는 item 의 총 개수를 반환한다.
        return todayMissionHistoryList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        // 위의 onCreateViewHolder 에서 만든 view 와 실제 입력되는 각각의 데이터를 연결한다.
        holder.bind(todayMissionHistoryList[position],context)
    }

}
