package com.example.faceapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProfileAdapter (private val context: Context, private val listener: deleteListener) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>()
{
    val allUser = ArrayList<Profile>()
    inner class ProfileViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    {
        val itemname = itemView.findViewById<TextView>(R.id.itemname)
        val itemage  = itemView.findViewById<TextView>(R.id.itemage)
        val itempofession = itemView.findViewById<TextView>(R.id.itemProfession)
        val itemsetprofilepicture = itemView.findViewById<ImageView>(R.id.itemsetprofilepicture)
        val itemimage = itemView.findViewById<ImageView>(R.id.itemimage)
        val delete = itemView.findViewById<ImageView>(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileAdapter.ProfileViewHolder {
        val viewHolder = ProfileViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_profile,parent,false))

        viewHolder.delete.setOnClickListener{
            listener.onItemClicked(allUser[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val currentUser = allUser[position]

        val nameEncrypted = currentUser.name
        val nameIv = currentUser.nameIV
        val decryptedName = CryptoManager().decrypt(Pair(nameEncrypted,nameIv))

        val ageEncrypted = currentUser.age
        val ageIv = currentUser.ageIV
        val decryptedAge = CryptoManager().decrypt(Pair(ageEncrypted,ageIv))

        val professionEncrypted = currentUser.profession
        val professionIv = currentUser.professionIV
        val decryptedProfession = CryptoManager().decrypt(Pair(professionEncrypted,professionIv))

        val pictureEncrypted = currentUser.picture
        val pictureIv = currentUser.pictureIV
        val decryptedPicture = CryptoManager().decrypt(Pair(pictureEncrypted,pictureIv))


        holder.itemname.text = decryptedName.decodeToString()
        holder.itemage.text = decryptedAge.decodeToString()
        holder.itempofession.text = decryptedProfession.decodeToString()
        val pictureUri = decryptedPicture.decodeToString().toUri()
        Glide.with(holder.itemView.context)
            .load(pictureUri)
            .into(holder.itemsetprofilepicture)
        Glide.with(context)
            .load(pictureUri)
            .into(holder.itemimage)
    }

    override fun getItemCount(): Int {
        return allUser.size
    }

    fun updateList(newList : List<Profile>)
    {
        allUser.clear()
        allUser.addAll(newList)
        notifyDataSetChanged()
    }
    fun getUserPosition(position: Int) :Profile{
        return allUser[position]
    }

}
interface deleteListener {
    fun  onItemClicked(user: Profile)
}
