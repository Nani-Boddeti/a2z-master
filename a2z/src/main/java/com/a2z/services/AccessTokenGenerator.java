package com.a2z.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AccessTokenGenerator {

    public String getAccessTokenWithRefreshToken(String refreshTokenParam , HttpServletRequest request , HttpServletResponse httpServletResponse) {
    	try {
            // URL to send the request to
            URL url = new URL("http://localhost:8080/oauth2/token");

            // Open connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set request method to POST
            conn.setRequestMethod("POST");

            // Set request property to indicate the content type
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Enable input and output streams
            conn.setDoOutput(true);

            // Prepare the form data
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("grant_type", "refresh_token");
            params.put("client_id", "oidc-client");
            params.put("refresh_token", refreshTokenParam);
            params.put("client_secret", "secret");
            
            boolean first = true;StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, String> param : params.entrySet()) {
                String keystr = URLEncoder.encode(param.getKey(), "UTF-8");
                String paramstr = URLEncoder.encode(param.getValue(), "UTF-8");
                if ( first ) { first = false;
                } else {
                    sb.append("&");
                }
                sb.append(keystr).append("=").append(paramstr);
            }
            String urlParameters = sb.toString();

            // Write the form data to the output stream
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = urlParameters.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            	if(!HttpStatus.OK.isSameCodeAs(HttpStatus.resolve(responseCode))) {
            		throw new Exception("Auth failed");
            	}
            // Read the response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                JSONObject tokenJson = new JSONObject(response.toString());
                String accessToken = (String) tokenJson.get("access_token");
                String refreshToken = (String) tokenJson.get("refresh_token");
                List<Cookie> cookies = new ArrayList<>();
                cookies.add(new Cookie("access_token", accessToken));
                cookies.add(new Cookie("refresh_token", refreshToken));
                cookies.forEach(c -> httpServletResponse.addCookie(c));
                //System.out.println("Response: " + response.toString());
                request.getSession().setAttribute("accessToken", accessToken);
                request.getSession().setAttribute("refreshToken", refreshToken);
                conn.disconnect();
                return accessToken;
            }

        } catch (Exception e) {
            e.printStackTrace();
            
        }
    	
    	
    	return "Empty";
    }
    
}
