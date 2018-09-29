package andreasneokleous.com.securepathservice.dao;

import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import andreasneokleous.com.securepathservice.model.CrimeLocation;
import com.google.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;


/**
 * @author Andreas Neokleous
 */

public class CrimeDAOImpl implements CrimeDAO {

    private JdbcTemplate jdbcTemplate = null;
    private String noConnection = "Please start thrift server";

    public CrimeDAOImpl(DataSource dataSource) {
        try {
            if (dataSource.getConnection() != null) {
                jdbcTemplate = new JdbcTemplate(dataSource);
            }
        } catch (SQLException ex) {
            System.out.println(noConnection);
        }

    }

    @Override
    public List<CrimeLocation> crimesOnLocations(List<LatLng> route) {
        List<CrimeLocation> crimeLocations = new ArrayList<>();
        String sql = "";
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM crimesum WHERE ");

        for (int i = 0; i < route.size() - 1; i++) {
            sqlBuilder.append("((Latitude BETWEEN LEAST(" + route.get(i).lat + "," + route.get(i + 1).lat + ") AND GREATEST(" + route.get(i).lat + "," + route.get(i + 1).lat + ") )"
                    + " AND ( Longitude BETWEEN LEAST(" + route.get(i).lng + "," + route.get(i + 1).lng + ") AND GREATEST(" + route.get(i).lng + "," + route.get(i + 1).lng + ") ))");
            if (i + 1 != route.size() - 1) {
                sqlBuilder.append(" OR ");
            }
        }
        sql = sqlBuilder.toString();

        System.out.println(sql);

        if (jdbcTemplate != null) {
            crimeLocations = jdbcTemplate.query(sql, new RowMapper<CrimeLocation>() {
                @Override
                public CrimeLocation mapRow(ResultSet rs, int i) throws SQLException {
                    CrimeLocation location = new CrimeLocation();

                    location.setLatitude(rs.getDouble("Latitude"));
                    location.setLongitude(rs.getDouble("Longitude"));
                    location.setSeriousness(rs.getInt("Seriousness"));
                    location.setCrime_count(rs.getInt("Crime_Count"));

                    return location;
                }
            });
        } else {
            System.out.println(noConnection);
        }

        return crimeLocations;
    }
}
