package net.azarquiel.listacompra_v2

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.EditText
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import net.azarquiel.listacompra_v2.adapter.CustomAdapter
import net.azarquiel.listacompra_v2.model.ListaCompra
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity() {
    lateinit var listadoCompra:ArrayList<ListaCompra>
    lateinit var lista: SharedPreferences
    lateinit var nombre: EditText
    lateinit var descripcion: EditText
    lateinit var adapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        lista = getSharedPreferences("listaCompra", Context.MODE_PRIVATE)
        rellenarLista()
        pintarRecicleView()
        fab.setOnClickListener {nuevaCompra()}
    }

    private fun rellenarLista() {
        val compraShare=lista.all
        listadoCompra= arrayListOf()
        for(entry in compraShare.entries) {
            val jsonCompra = entry.value.toString()
            val compra = Gson().fromJson(jsonCompra, ListaCompra::class.java)
            listadoCompra.add(compra)
        }
    }

    private fun pintarRecicleView() {
        adapter=CustomAdapter(this,R.layout.rowcompra,this)
        rvCompra.layoutManager= LinearLayoutManager(this)
        rvCompra.adapter=adapter
    }

    private fun nuevaCompra() {
        alert("Introduce el nombre y la descripcion de una compra") {
            positiveButton("Añadir compra a la lista"){añadirCompra()}
            customView {
                verticalLayout{
                    nombre=editText{
                        hint=getString(R.string.nombre)
                    }
                    descripcion=editText {
                        hint = getString(R.string.descripcion)
                    }
                    padding = dip(10)
                }
            }
        }.show()
    }

    private fun añadirCompra() {
        if(nombre.text.isEmpty()){
            nuevaCompra()
            toast(R.string.nulo)
        }else{
            val compra=ListaCompra(nombre.text.toString(),descripcion.text.toString(),false)
            val jsonCompra:String= Gson().toJson(compra)
            val editor=lista.edit()
            editor.putString(compra.nombre,jsonCompra)
            editor.commit()
            listadoCompra.add(compra)
            adapter.notifyDataSetChanged()
            toast(R.string.añadida)
        }
    }

    fun pulsarParaEditar(v:View){
        val producto=v.tag as ListaCompra
        alert("Cambia el nombre o la descripción del producto") {
            positiveButton("Modificar"){editarProducto(producto,v)}
            negativeButton("Cancelar"){}
            customView {
                verticalLayout{
                    textView("Nombre:")
                    nombre=editText{
                        text=SpannableStringBuilder(producto.nombre)
                    }
                    textView("Descripcion:")
                    descripcion=editText {
                        text=SpannableStringBuilder(producto.descripcion)
                    }
                    padding = dip(10)
                }
            }
        }.show()
    }

    private fun editarProducto(producto: ListaCompra, v: View) {
        if(!nombre.text.isEmpty()) {
            val productoNuevo = ListaCompra(nombre.text.toString(), descripcion.text.toString(), producto.comprado);
            val editor = lista.edit()
            listadoCompra.set(listadoCompra.indexOf(producto), productoNuevo)
            editor.remove(producto.nombre)
            val jsonProducto: String = Gson().toJson(productoNuevo)
            editor.putString(productoNuevo.nombre, jsonProducto)
            editor.commit()
            adapter.notifyDataSetChanged()
            toast(R.string.editada)
        }else{
            pulsarParaEditar(v)
            toast("No puedes editar el nombre a nulo")
        }
    }
}
