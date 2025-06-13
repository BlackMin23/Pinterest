package com.pinterest.pinterestfirebase.ui.publicacion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pinterest.pinterestfirebase.data.model.PublicacionN
import com.pinterest.pinterestfirebase.databinding.ItemPubliNBinding

class PublicacionNAdapter  (
    private val onEditClick: (PublicacionN) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : ListAdapter<PublicacionN, PublicacionNAdapter.PubliNViewHolder>(PubliNDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PubliNViewHolder {
        // Infla el layout de cada elemento de la lista
        val binding = ItemPubliNBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PubliNViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PubliNViewHolder, position: Int) {
        // Vincula los datos de la mascota en la posición actual al ViewHolder
        val mascota = getItem(position)
        holder.bind(mascota)
    }

    inner class PubliNViewHolder(private val binding: ItemPubliNBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Configura los listeners para los botones de editar y eliminar

            binding.btnEditPuN.setOnClickListener {
                onEditClick(getItem(adapterPosition))
            }
            binding.btnDeletePuN.setOnClickListener {
                onDeleteClick(getItem(adapterPosition).id)
            }

        }

        fun bind(publiN: PublicacionN) {
            // Asigna los datos de la mascota a las vistas correspondientes

            binding.tvNombre.text = publiN.name
            binding.tvPublNDes.text = "Descripcion: ${publiN.descripcion}"

        }
    }

    /**
     * Callback para calcular las diferencias entre dos listas de mascotas.
     * Mejora el rendimiento del RecyclerView al solo actualizar los elementos que han cambiado.
     */

    class PubliNDiffCallback : DiffUtil.ItemCallback<PublicacionN>() {
        override fun areItemsTheSame(oldItem: PublicacionN, newItem: PublicacionN): Boolean {
            // Compara si los IDs de los elementos son los mismos (mismo elemento)
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PublicacionN, newItem: PublicacionN): Boolean {
            // Compara si el contenido de los elementos es el mismo (no hay cambios visuales)
            return oldItem == newItem
        }
    }

}