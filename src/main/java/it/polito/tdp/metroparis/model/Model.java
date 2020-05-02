package it.polito.tdp.metroparis.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	private Graph<Fermata,DefaultEdge > graph;
	private List<Fermata> fermate;
	private Map<Integer,Fermata> fermateIdMap;
	
	
	public Model() {
		graph = new SimpleDirectedGraph<>(DefaultEdge.class);
		
		MetroDAO dao=new MetroDAO();
		
		//creazione vertici
		this.fermate=dao.getAllFermate();
		this.fermateIdMap=new HashMap<>();
		for(Fermata f:fermate) {
			fermateIdMap.put(f.getIdFermata(), f);
		}
		
		
		Graphs.addAllVertices(this.graph, this.fermate);
		
		
		//Creazione archi--metodo 1 coppie vertici 
		/*
		for(Fermata fp: this.fermate) {
			for( Fermata fa:this.fermate) {
				if(dao.fermateConnesse(fp, fa)) {
					graph.addEdge(fp,fa);
				}
			}
		}*/
		
		// Creazione archi-- metodo 2 da un vertice i conessi
		/*
		for(Fermata fp:this.fermate) {
		List<Fermata> connesse = dao.fermateSuccessive(fp, fermateIdMap);
				for(Fermata fa :connesse) {
					graph.addEdge(fp, fa);
				}
		}*/
		
		//Creazione archi-- metodo 3 chiedo elenco archi al database
		List<Coppie> coppie = dao.coppieFermate(fermateIdMap);
		for(Coppie c: coppie) {
			graph.addEdge(c.getFp(), c.getFa());
		}
		
		System.out.println(graph.vertexSet().size()+"    "+graph.edgeSet().size());
	}
	
	public static void main(String args[]) {
		Model m=new Model();
	}

}
