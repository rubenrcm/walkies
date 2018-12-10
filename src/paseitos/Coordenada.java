package paseitos;

public class Coordenada {
    
    Double lat;
    Double lng;
    
    Coordenada(Double lat, Double lng){
        this.lat = lat;
        this.lng = lng;
    }
    
    public Double getLatitud(){
        return this.lat;
    }
    
    public Double getLongitud(){
        return this.lng;
    }

    Double getAngulo(Coordenada B) {
        //Integer angulo = 0;
        
        Coordenada C = new Coordenada(this.lat,B.getLongitud());
        Double adyacente = Math.sqrt(Math.pow(C.getLatitud()-this.lat,2)+Math.pow(C.getLongitud()-this.lng,2)); //A-C
        Double opuesto = Math.sqrt(Math.pow(B.getLatitud()-C.getLatitud(),2)+Math.pow(B.getLongitud()-C.getLongitud(),2)); //C-B
        
        /*
        anguloRadianes = Math.atan(valor);
        angulo = Math.toDegrees(anguloRadianes);
        System.out.println("Arco Tangente de " + valor + " = " + angulo + "Âº");
        */
        
        Double anguloRadianes = Math.atan(opuesto/adyacente);
        Double angulo = Math.toDegrees(anguloRadianes);
        //System.out.println("Arco Tangente de " + opuesto/adyacente + " = " + angulo + "Âº");        

        //System.out.println(adyacente.toString() + " - " + opuesto.toString() + " = " + opuesto/adyacente);
        //System.out.println(90 - Math.toDegrees(opuesto/adyacente));
        
        return angulo;
    }
}
