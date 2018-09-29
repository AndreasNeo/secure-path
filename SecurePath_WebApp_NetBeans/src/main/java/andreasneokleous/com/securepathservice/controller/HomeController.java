package andreasneokleous.com.securepathservice.controller;

import andreasneokleous.com.securepathservice.dao.CrimeDAO;
import andreasneokleous.com.securepathservice.model.CrimeLocation;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;
import java.awt.Button;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Andreas Neokleous
 */

@Controller
public class HomeController {

    @Autowired
    private CrimeDAO crimeDAO;

    @RequestMapping(value = "/home")
    public ModelAndView listCrime(ModelAndView model) throws IOException {
        model.setViewName("home");
        return model;
    }

    @RequestMapping(value = "/polyline", method = RequestMethod.GET)
    public @ResponseBody
    List<CrimeLocation> getMultiLatLng(@RequestParam String polyline) {
        List<CrimeLocation> locations = new ArrayList<>();
        List<LatLng> pathLatLng = null;
        EncodedPolyline encodedPolyline = new EncodedPolyline(polyline);
        pathLatLng = encodedPolyline.decodePath();
        locations = crimeDAO.crimesOnLocations(pathLatLng);
        System.out.println(pathLatLng);

        return locations;
    }

    @RequestMapping(value = "/example", method = RequestMethod.GET)
    public @ResponseBody
    List<CrimeLocation> example() {
        List<CrimeLocation> locations = new ArrayList<>();
        ArrayList<LatLng> list = new ArrayList<>();
        list.add(new LatLng(51.570761, 0.136078));
        list.add(new LatLng(51.571724, 0.135842));
        list.add(new LatLng(51.572024, 0.138765));
        list.add(new LatLng(51.569507, 0.138814));
        locations = crimeDAO.crimesOnLocations(list);
        return locations;
    }

}
