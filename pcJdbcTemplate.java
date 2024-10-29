package com.example.demo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class pcJdbcTemplate {


    private JdbcTemplate jdbcTemplateObject;

    @Autowired
    public void setJdbcTemplateObject(JdbcTemplate jdbcTemplateObject) {
        this.jdbcTemplateObject = jdbcTemplateObject;
        
    }

    public int insertPc(String nome, String marca, String descrizione, double prezzo, String url, int qntMagazzino, int qntVenduti) {
        String query = "INSERT INTO tabellapc (nome, marca, descrizione, prezzo, url, qntMagazzino, qntVenduti) VALUES (?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplateObject.update(query, nome, marca, descrizione, prezzo, url, qntMagazzino, qntVenduti);
       
    }

    public int delete(String nome) {
        String query = "DELETE FROM tabellapc WHERE nome = ?";
        return jdbcTemplateObject.update(query, nome);
    }

    public ArrayList<pc> getLista() {
        String query = "SELECT * FROM tabellapc";

        return jdbcTemplateObject.query(query, new ResultSetExtractor<ArrayList<pc>>() {

        	@Override
            public ArrayList<pc> extractData(ResultSet rs) throws SQLException, DataAccessException {
                ArrayList<pc> listaPc = new ArrayList<>();

                while (rs.next()) {
                    pc pc1 = new pc();
                    pc1.setNome(rs.getString("nome"));
                    pc1.setMarca(rs.getString("marca"));
                    pc1.setDescrizione(rs.getString("descrizione"));
                    pc1.setPrezzo(rs.getDouble("prezzo"));
                    pc1.setUrl(rs.getString("url"));
                    pc1.setQntMagazzino(rs.getInt("qntMagazzino"));
                    pc1.setQntVenduti(rs.getInt("qntVenduti"));

                    listaPc.add(pc1);
                }

                return listaPc;
            }
        });
    }

public int updateQnt(int qnt, String nome) {
    String query = "UPDATE tabellapc SET qntVenduti = qntVenduti + ? WHERE nome = ?";
    return jdbcTemplateObject.update(query, qnt, nome);
}

public int updateQntMagazzino(int qnt, String nome) {
    String query = "UPDATE tabellapc SET qntMagazzino = qntMagazzino - ? WHERE nome = ?";
    return jdbcTemplateObject.update(query, qnt, nome);
}


}