package example.server;

import example.RouteMapper;
import example.framework.response.JsonResponse;
import example.framework.response.Response;
import example.framework.request.enums.MethodEnum;
import example.framework.request.Header;
import example.framework.request.Helper;
import example.framework.request.Request;
import example.framework.request.exceptions.RequestNotValidException;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerThread implements Runnable{

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private RouteMapper routeMapper;

    public ServerThread(Socket socket, RouteMapper routeMapper) {
        this.socket = socket;
        this.routeMapper = routeMapper;
        try {
            in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try {

            Request request = this.generateRequest();
            if(request == null) {
                in.close();
                out.close();
                socket.close();
                return;
            }

            routeMapper.sendRequest(request.getMethod() + " " + request.getLocation());

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("route_location", request.getLocation());
            responseMap.put("route_method", request.getMethod().toString());

            responseMap.put("parameters", request.getParameters());
            Response response = new JsonResponse(responseMap);

            out.println(response.render());

            in.close();
            out.close();
            socket.close();

        } catch (IOException | RequestNotValidException e) {
            e.printStackTrace();
        }
    }

    private Request generateRequest() throws IOException, RequestNotValidException {
        String command = in.readLine();
        if(command == null) {
            return null;
        }

        String[] actionRow = command.split(" ");
        System.out.println("Method: " + actionRow[0]);
        System.out.println("Command: " + command);

        MethodEnum method = MethodEnum.valueOf(actionRow[0]);
        String route = actionRow[1];
        Header header = new Header();
        HashMap<String, String> parameters = Helper.getParametersFromRoute(route);

        do {
            command = in.readLine();
            String[] headerRow = command.split(": ");
            if(headerRow.length == 2) {
                header.add(headerRow[0], headerRow[1]);
            }
        } while(!command.trim().equals(""));

        if(method.equals(MethodEnum.POST)) {
            if(header.get("content-length") != null) {
                int contentLength = Integer.parseInt(header.get("content-length"));

                char[] buff = new char[contentLength];
                in.read(buff, 0, contentLength);
                String parametersString = new String(buff);

                HashMap<String, String> postParameters = Helper.getParametersFromString(parametersString);
                for (String parameterName : postParameters.keySet()) {
                    parameters.put(parameterName, postParameters.get(parameterName));
                }
            }

        }

        Request request = new Request(method, route, header, parameters);

        return request;
    }
}
