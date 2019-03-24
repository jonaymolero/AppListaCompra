package net.azarquiel.listacompra_v2.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import kotlinx.android.synthetic.main.rowcompra.view.*
import net.azarquiel.listacompra_v2.MainActivity
import net.azarquiel.listacompra_v2.model.ListaCompra
import org.jetbrains.anko.*

class CustomAdapter(val context: Context,
                    val layout: Int,
                    val main:MainActivity) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context, main)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = main.listadoCompra[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return main.listadoCompra.size
    }

    class ViewHolder(viewlayout: View, val context: Context, val main: MainActivity) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: ListaCompra){
            // itemview es el item de diseño
            // al que hay que poner los datos del objeto dataItem
            itemView.tvNombre.setOnCheckedChangeListener(null)
            itemView.tvNombre.text=dataItem.nombre
            itemView.tvNombre.isChecked=dataItem.comprado
            itemView.tvCantidad.text=dataItem.descripcion
            itemView.setTag(dataItem)
            itemView.setOnLongClickListener{ mantenerPulsadoParaBorrar(dataItem)}
            itemView.tvNombre.setOnCheckedChangeListener{view,isChecked->chequeado(dataItem)}
        }

        private fun chequeado(dataItem: ListaCompra) {
            dataItem.comprado=!dataItem.comprado
            val editor=main.lista.edit()
            val jsonProducto:String=Gson().toJson(dataItem)
            editor.putString(dataItem.nombre,jsonProducto)
            editor.commit()
        }

        private fun mantenerPulsadoParaBorrar(dataItem: ListaCompra): Boolean {
            context.alert ("¿Quieres borrar el producto?") {
                title="Eliminar"
                yesButton {
                    borrarProducto(dataItem)
                    context.toast("Producto eliminado")
                }
                noButton { context.toast("Cencelado") }
            }.show()
            return true
        }

        private fun borrarProducto(dataItem: ListaCompra) {
            val editor=main.lista.edit()
            editor.remove(dataItem.nombre)
            editor.commit()
            main.listadoCompra.remove(dataItem)
            main.adapter.notifyDataSetChanged()
        }
    }
}