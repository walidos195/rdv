package com.hopital.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.googlecode.s2hibernate.struts2.plugin.annotations.SessionTarget;
import com.googlecode.s2hibernate.struts2.plugin.annotations.TransactionTarget;
import com.hopital.domain.Rdv;

public class Rdvmdlimp implements Rdvmdl {
	@SessionTarget
	Session session;
	@TransactionTarget
	Transaction transaction;

	// m�thode d'insertion de rdv (requ�te insert into)
	@Override
	public void saveRdv(Rdv rdv) {
		// TODO Auto-generated method stub

		String hql = "INSERT INTO rendezvous (iduser, idpatient, date, heure, duree) "
				+ "VALUES ('"
				+ rdv.getIduser()
				+ "', '"
				+ rdv.getIdpatient()
				+ "', '"
				+ rdv.getDate()
				+ "', '"
				+ rdv.getHeure()
				+ "', '"
				+ rdv.getDuree() + "');";

		try {
			session.createSQLQuery(hql).executeUpdate();

		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		}
	}

	// m�thode de r�cup�ration (requ�te select)
	@Override
	public List<Rdv> listRdv() {
		// TODO Auto-generated method stub
		List<Rdv> rdv = null;
		try {

			rdv = session.createQuery("from Rdv").list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rdv;
	}

	// m�thode de r�cup�ration avec une date pr�cise et un identifiant du
	// patient ou du m�decin (pour la m�thode check du controleur)
	@Override
	public List<Rdv> listRdv(Rdv rd) {
		// TODO Auto-generated method stub
		List<Rdv> rdvl = null;
		try {

			Query query = session
					.createQuery("from Rdv where date=:date and (idpatient=:idpatient OR iduser=:iduser)");
			query.setParameter("date", rd.getDate());
			query.setParameter("idpatient", rd.getIdpatient());
			query.setParameter("iduser", rd.getIduser());
			rdvl = query.list();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return rdvl;
	}

	// m�thode de r�cup�ration avec des conditions (recherche)
	@Override
	public List<Rdv> listCustom(Rdv rdv) {
		List<Rdv> rdvl = null;
		ArrayList<String> rq = new ArrayList<String>();
		String sql = " from Rdv ";
		try {

			if (rdv.getDate() != null) {
				rq.add(" date='" + rdv.getDate().toString() + "' ");
			}
			if (rdv.getHeure() != null) {
				rq.add(" heure='" + rdv.getHeure().toString() + "' ");
			}
			if (rdv.getDuree() != null) {
				rq.add(" duree=" + rdv.getDuree() + " ");
			}
			if (rdv.getIdpatient() != 0) {
				rq.add(" idpatient=" + rdv.getIdpatient() + " ");
			}
			if (rdv.getIduser() != 0) {
				rq.add(" iduser=" + rdv.getIduser() + " ");
			}
			if (rdv.getId() != 0) {
				rq.add(" id=" + rdv.getId() + " ");
			}
			if (!rq.isEmpty()) {
				sql += " where ";
			}
			for (int i = 0; i < rq.size(); i++) {
				sql += rq.get(i);
				if (i != rq.size() - 1) {
					sql += " AND ";
				}
			}
			Query query = session.createQuery(sql);
			rdvl = query.list();
		} catch (HibernateException e) {
			e.printStackTrace();
		}
		return rdvl;
	}

	// m�thode de modification de rdv (requ�te update)
	public void updateRdv(Rdv rdv) {
		// TODO Auto-generated method stub

		String hql = "UPDATE rendezvous SET " + " date = '" + rdv.getDate()
				+ "' , idpatient = '" + rdv.getIdpatient() + "' , iduser = '"
				+ rdv.getIduser() + "', heure= '" + rdv.getHeure()
				+ "', duree = '" + rdv.getDuree() + "' WHERE id='"
				+ rdv.getId() + "'";

		try {
			session.createSQLQuery(hql).executeUpdate();

		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		}
	}

	// suppression du rendez-vouys � partir de son id (requ�te DELETE)
	@Override
	public void delete(String get) {
		// TODO Auto-generated method stub
		String hql = "DELETE FROM rendezvous WHERE id ='" + get + "' ";

		try {
			session.createSQLQuery(hql).executeUpdate();

		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		}
	}

}
