package com.hour.onegoal

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hour.onegoal.Data.Mission
import com.hour.onegoal.Data.TodayMission
import com.hour.onegoal.Util.loadImage
import com.makeramen.roundedimageview.RoundedImageView
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text
import soup.neumorphism.NeumorphCardView
import soup.neumorphism.NeumorphTextView


class TodayMissionHistoryAdapter(
    val context: Context, private val todayMissionHistoryList: ArrayList<TodayMission>, private val photoUrl: ArrayList<ArrayList<String>>?,
    var itemClick: (TodayMission) -> Unit)
    : RecyclerView.Adapter<TodayMissionHistoryAdapter.Holder>() {

    inner class Holder(itemView: View?, itemClick: (TodayMission) -> Unit) : RecyclerView.ViewHolder(itemView!!){

        val todayMissiontitle = itemView?.findViewById<TextView>(R.id.item_todayMissionTitle_TextView)
        val todayMissionDate = itemView?.findViewById<TextView>(R.id.item_todayMissionDate_TextView)

        private fun getId(itemView: View?):ArrayList<RoundedImageView>{
            val result: ArrayList<RoundedImageView> = ArrayList()
            for(i in 0..7){
                result.add(itemView!!.findViewById(context.resources.getIdentifier("missionPhoto_$i","id",context.packageName)))
                //Log.d("근바", "${itemView.findViewById<RoundedImageView>(context.resources.getIdentifier("missionPhoto_0","id",context.packageName))}")
            }
            return result
        }

        private val missionUserPhotoUrl: ArrayList<RoundedImageView> = getId(itemView)

        //itemView?.findViewById<RoundedImageView>(R.id.missionPhoto_1)
        fun bind(todayMission: TodayMission, context: Context, photo:ArrayList<String>){
            todayMissiontitle?.text = todayMission.todaymissionTitle
            todayMissionDate?.text =  todayMission.todaymissionDate
            photo.forEach{
                Log.d("TAG : ", "$it")
            }
            var count = 0
            for(i in photo.indices){
               missionUserPhotoUrl[i].loadImage(photo[i])
                count += 1
            }
            Log.d("COUNT : ","$count")
            itemView.findViewById<NeumorphCardView>(R.id.missionCardView7).visibility = View.INVISIBLE
            /*
            for(i in count..7){
                Log.d("MissionUser : ", "${missionUserPhotoUrl[i].visibility}")
            }*/

        }

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

    var count = 0
    override fun onBindViewHolder(holder: Holder, position: Int) {
        // 위의 onreateViewHolder 에서 만든 view 와 실제 입력되는 각각의 데이터를 연결한다.
        holder.bind(todayMissionHistoryList[position],context,photoUrl?.get(position)!!)
    }

}
