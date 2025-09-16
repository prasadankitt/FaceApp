package com.example.faceapp.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.faceapp.R
import com.example.faceapp.data.Profile
import com.example.faceapp.utils.CryptoManager

class ProfileAdapter (private val context: Context, private val listener: deleteListener) : 
    RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {
    
    private var profiles = mutableListOf<Profile>()
    
    inner class ProfileViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val itemname = itemView.findViewById<TextView>(R.id.itemname)
        val itemage  = itemView.findViewById<TextView>(R.id.itemage)
        val itempofession = itemView.findViewById<TextView>(R.id.itemProfession)
        val itemsetprofilepicture = itemView.findViewById<ImageView>(R.id.itemsetprofilepicture)
        val itemimage = itemView.findViewById<ImageView>(R.id.itemimage)
        val delete = itemView.findViewById<ImageView>(R.id.delete)
        
        private var cachedProfile: Profile? = null
        private var cachedDecryptedData: DecryptedProfile? = null
        
        fun bind(profile: Profile) {
            if (cachedProfile != profile) {
                cachedProfile = profile
                cachedDecryptedData = decryptProfile(profile)
            }
            val decryptedData = cachedDecryptedData!!
            itemname.text = decryptedData.name
            itemage.text = decryptedData.age
            itempofession.text = decryptedData.profession
            val pictureUri = decryptedData.pictureUri
            
            Glide.with(itemView.context)
                .load(pictureUri)
                .error(android.R.drawable.ic_menu_report_image)
                .into(itemsetprofilepicture)
            Glide.with(context)
                .load(pictureUri)
                .error(android.R.drawable.ic_menu_report_image)
                .into(itemimage)
        }
        
        private fun decryptProfile(profile: Profile): DecryptedProfile {
            val nameDecrypted = CryptoManager().decrypt(Pair(profile.name, profile.nameIV))
            val ageDecrypted = CryptoManager().decrypt(Pair(profile.age, profile.ageIV))
            val professionDecrypted = CryptoManager().decrypt(Pair(profile.profession, profile.professionIV))
            val pictureDecrypted = CryptoManager().decrypt(Pair(profile.picture, profile.pictureIV))
            return DecryptedProfile(
                name = nameDecrypted.decodeToString(),
                age = ageDecrypted.decodeToString(),
                profession = professionDecrypted.decodeToString(),
                pictureUri = pictureDecrypted.decodeToString().toUri()
            )
        }
    }
    
    data class DecryptedProfile(
        val name: String,
        val age: String,
        val profession: String,
        val pictureUri: android.net.Uri
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val viewHolder = ProfileViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_profile,parent,false))
        viewHolder.delete.setOnClickListener{
            val position = viewHolder.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClicked(profiles[position])
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(profiles[position])
    }

    override fun getItemCount(): Int {
        return profiles.size
    }
    
    fun updateList(newList: List<Profile>) {
        profiles.clear()
        profiles.addAll(newList)
        notifyDataSetChanged()
    }
    
    fun getUserPosition(position: Int): Profile {
        return profiles[position]
    }
}

interface deleteListener {
    fun  onItemClicked(user: Profile)
}
