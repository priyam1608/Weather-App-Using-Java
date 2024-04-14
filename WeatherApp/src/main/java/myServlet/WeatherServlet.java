package myServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class WeatherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public WeatherServlet() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String city=request.getParameter("city");
//		System.out.println(city);
		
		String ApiKey ="594dd64049581272e3bdf72972cfe2ea";
		String ApiUrl ="https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+ApiKey;
		
		// DATA GET FROM OPEN WEATHER API / API INTEGRATION.
		try {
			URL url=new URL(ApiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			
			InputStream inputstream=connection.getInputStream();
			InputStreamReader reader = new InputStreamReader(inputstream);
			
			Scanner sc= new Scanner(reader);
			StringBuilder responseContent =new StringBuilder();
			
			while(sc.hasNext()) {
				responseContent.append(sc.nextLine());
			}
			
//			System.out.println(responseContent);
			sc.close();
			
			//Parsing the JSON response to extract time,temperature,windspeed,etc.
			Gson gs= new Gson();
			JsonObject jsonObject = gs.fromJson(responseContent.toString(), JsonObject.class);
			
			//temperature
			double TemperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
			int TemperatureCelsius=(int) (TemperatureKelvin - 273.15);
			
			// Windspeed
			double windSpeed =jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
			
			//humidity
			double humidity =jsonObject.getAsJsonObject("main").get("humidity").getAsDouble();
			
			//timestamp
			long timestamp =jsonObject.get("dt").getAsLong() *1000;
			Date date = new Date(timestamp);
			
			//WeatherCondition
			String WeatherCondition =jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").toString();
			
			
			request.setAttribute("dateTime", date);
			request.setAttribute("temperature", TemperatureCelsius);
			request.setAttribute("windspeed",windSpeed);
			request.setAttribute("humidity", humidity);
			request.setAttribute("weatherCondition", WeatherCondition);
			request.setAttribute("city", city);
			request.setAttribute("weatherData", responseContent.toString());
			
			connection.disconnect();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		// Forward the request to the weather.jsp page for rendering
        request.getRequestDispatcher("index.jsp").forward(request, response);
        
		
	}

}
