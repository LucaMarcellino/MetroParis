package it.polito.tdp.metroparis.model;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.TraversalListenerAdapter;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

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
		
		
		//Creazione archi--metodo 1 coppie vertici da evitare di usare troppo lungo per dati maggiori di un centinaio
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
	
	
	public List<Fermata> visitaAmpiezza(Fermata source) {
		
		List<Fermata> visita = new ArrayList<>();
		
		BreadthFirstIterator<Fermata,DefaultEdge> bfv = new BreadthFirstIterator<>(graph,source);
		while(bfv.hasNext()) {
			visita.add(bfv.next());
			
		}
		
		return visita;

		
	}
	
	public List<Fermata> visitaProfondita(Fermata source) {
		
		List<Fermata> visita = new ArrayList<>();
		
		DepthFirstIterator<Fermata,DefaultEdge> dfv = new DepthFirstIterator<>(graph,source);
		while(dfv.hasNext()) {
			visita.add(dfv.next());
			
		}
		
		return visita;

		
	}
	
	public Map<Fermata,Fermata>  alberoVisita(Fermata source) {
		 final Map<Fermata,Fermata> albero = new HashMap<>();
		 albero.put(source, null);
		 
		 BreadthFirstIterator<Fermata,DefaultEdge> bfv = new BreadthFirstIterator<>(graph,source);
		
		
			
		
		bfv.addTraversalListener( new TraversalListener<Fermata, DefaultEdge>() {
			
			@Override
			public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
				
				
			}
			
			@Override
			public void vertexFinished(VertexTraversalEvent<Fermata> e) {
				
				
			}
			
			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
				DefaultEdge edge= new DefaultEdge();
				Fermata a = graph.getEdgeSource(edge);
				Fermata b=graph.getEdgeTarget(edge);
				if(albero.containsKey(a)) {
					albero.put(b,a);
				}
				else{
					albero.put(a, b);
				}
				
			}
			
			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
				
				
			}
			
			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
				
				
			}
		});
		
		
		while(bfv.hasNext()) {
			bfv.next();
			}
		
		
		
		
		
		return albero;
		
	

	}
	
	public List<Fermata> camminiMinini(Fermata partenza,Fermata arrivo) {
		 DijkstraShortestPath<Fermata , DefaultEdge> dij= new DijkstraShortestPath<Fermata, DefaultEdge>(graph);
		 GraphPath<Fermata,DefaultEdge> camminiMinim= dij.getPath(partenza, arrivo);
		 return camminiMinim.getVertexList();
	}
	
	
	public static void main(String args[]) {
		Model m=new Model();
		List<Fermata> visita= m.visitaAmpiezza(m.fermate.get(0));
		//System.out.println(visita);
		List<Fermata> visita2= m.visitaProfondita(m.fermate.get(0));
		//System.out.println(visita2);
		
		Map<Fermata,Fermata> albero= m.alberoVisita(m.fermate.get(0));
		for(Fermata f : albero.keySet()) {
			//System.out.format("%s <- %s\n", f,albero.get(f) );
			
			List<Fermata> cammino = m.camminiMinini(m.fermate.get(0), m.fermate.get(1));
			System.out.println(cammino);
		}
	}

}
