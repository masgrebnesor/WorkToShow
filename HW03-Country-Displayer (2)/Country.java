import java.util.Map;
import java.util.HashMap;
public class Country
{
  String name;
  //stores information for the country
  Map<String, Float> data = new HashMap<String, Float>();

  
  //creates country
  public Country(String _name, Float _CO2Emissions, Float _AccessToElectricity, Float _RenewableEnergy, Float _ProtectedAreas, Float _PopulationGrowth, Float _PopulationTotal, Float _UrbanPopulationGrowth)
  {
    name = _name;
    data.put("CO2Emissions", _CO2Emissions);
    data.put("AccessToElectricity", _AccessToElectricity);
    data.put("RenewableEnergy", _RenewableEnergy);
    data.put("ProtectedAreas", _ProtectedAreas);
    data.put("PopulationGrowth", _PopulationGrowth);
    data.put("PopulationTotal", _PopulationTotal);
    data.put("UrbanPopulationGrowth", _UrbanPopulationGrowth);
  }
  // gets a certain value from the HashMap
  public Float sortingValue(String instanceToSort)
  {
    return data.get(instanceToSort);
  }
  // compares two of the countries in a certain aspect
  public boolean compareTo(Country otherCountry, String comparable) {
	
		Float compareQuantity = ((Country) otherCountry).sortingValue(comparable); 

		return this.sortingValue(comparable) >= compareQuantity;
	}
  // displays the information of a given country
  public void displayString()
  {
    System.out.println(name + ": " + data.get("CO2Emissions")+ data.get("AccessToElectricity")+ data.get("RenewableEnergy")+ data.get("ProtectedAreas")+ data.get("PopulationGrowth")+ data.get("PopulationTotal")+ data.get("UrbanPopulationGrowth"));
  }
}