package com.hopital.action;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.hopital.domain.Rdv;
import com.hopital.model.Rdvmdl;
import com.hopital.model.Rdvmdlimp;
import com.hopital.utility.*;

public class RdvAction extends ActionSupport implements ModelDriven<Rdv> {

	private static final long serialVersionUID = 1L;
	private Rdv rdv = new Rdv();
	private List<Rdv> rdvList = new ArrayList<Rdv>();
	private Rdvmdl rdvmdl = new Rdvmdlimp();
	private String viduser;
	private String vidpatient;
	private String vdate;
	private String vheure;
	private String vduree;

	public Rdv getModel() {
		return rdv;
	}

	// m�thode consacr� � l'ajout des rendez-vous
	@SuppressWarnings("deprecation")
	public String add() {
		// validation des donn�es
		if (this.valid() == INPUT) {
			return INPUT;
		}
		// formater les donn�es
		rdv.setHeure(new Time(rdv.getHour(), rdv.getMinute(), 0));
		rdv.setDate(DateUtil.getDate(rdv.getDaterdv()));

		if (check(rdv)) {
			rdvmdl.saveRdv(rdv);
			return SUCCESS;
		} else {
			return NONE;
		}
	}

	// r�cup�rer tous les rendez-vous qui existe (peut �tre lent s'il existe
	// beacoup de rendez-vous)
	public String list() {
		rdvList = rdvmdl.listRdv();
		return SUCCESS;
	}

	// m�thode consacr� � la r�cup�ration de la liste des rendez-vous d'une
	// recherche
	@SuppressWarnings("deprecation")
	public String get() {
		// mettre en forme l'heure
		if (rdv.getHour() != 00) {
			rdv.setHeure(new Time(rdv.getHour(), rdv.getMinute(), 0));
		}
		// mettre en forme la date
		if (!rdv.getDaterdv().isEmpty()) {
			rdv.setDate(DateUtil.getDate(rdv.getDaterdv()));
		}

		rdvList = rdvmdl.listCustom(rdv);
		return SUCCESS;

	}

	// m�thode consacr� � la modification des rendez-vous
	@SuppressWarnings("deprecation")
	public String set() {
		// hour + minute => Heure
		rdv.setHeure(new Time(rdv.getHour(), rdv.getMinute(), 0));
		// formater la date
		rdv.setDate(DateUtil.getDate(rdv.getDaterdv()));
		// modifier si le rendez-vous existe sinon retourn none
		if (check(rdv)) {
			rdvmdl.updateRdv(rdv);
			return SUCCESS;
		} else {
			return NONE;
		}
	}

	// m�thode consacr� a la suppression des rendez-vous
	public String delete() {
		// recupere l'id du rendez-vous (?id=xx)
		String get = ServletActionContext.getRequest().getParameter("id");
		Rdv rd = new Rdv();
		rd.setId(Integer.parseInt(get));
		// recupere le rdv de la base de donn�es qui l'id=xx
		rdvList = rdvmdl.listCustom(rd);
		// si la base de donn�e contient le rdv voulu on le supprime
		if (!rdvList.isEmpty()) {
			rdvmdl.delete(get);
			return SUCCESS;
		}
		// sinon on retourn none(erreur)
		else {
			return NONE;
		}
	}

	// m�thode importante qui permet de corriger le probl�me d'incoh�rence des
	// donn�es
	// �vite d'avoir deux rendez-vous au m�me moment par exemple
	public boolean check(Rdv rdv) {
		boolean bool = true;
		List<Rdv> rdvlist = this.listcheck(rdv);
		for (Rdv rdvv : rdvlist) {
			if (rdvv.getId() != rdv.getId()) {
				if (rdvv.getHeure().after((rdv.getHeure()))) {
					long diff = rdvv.getHeure().getTime()
							- rdv.getHeure().getTime();
					diff = Math.abs(diff) / 60000;
					if (diff >= rdv.getDuree()) {//
						bool = true;
					} else {
						bool = false;
						break;
					}
				} else if (rdvv.getHeure().before((rdv.getHeure()))) {
					long diff = rdvv.getHeure().getTime()
							- rdv.getHeure().getTime();
					diff = Math.abs(diff) / 60000;
					if (diff >= rdvv.getDuree()) {//
						bool = true;
					} else {
						bool = false;
						break;
					}
				} else {
					bool = false;
					break;
				}
			} else {
				bool = true;
			}
		}

		return bool;
	}

	// m�thode facultatif permettant de mettre les informations du rendez-vous
	// sur le formulaire
	public String listset() {
		// r�cup�re l'id � partir de l'url
		String get = ServletActionContext.getRequest().getParameter("id");
		Rdv rd = new Rdv();
		rd.setId(Integer.parseInt(get));
		// r�cup�rer le rdv dont l'id =id
		rdvList = rdvmdl.listCustom(rd);
		return SUCCESS;
	}

	public List<Rdv> listcheck(Rdv rdv) {
		return rdvmdl.listRdv(rdv);
	}

	public Rdv getRdv() {
		return rdv;
	}

	public void setRdv(Rdv rdv) {
		this.rdv = rdv;
	}

	public List<Rdv> getRdvList() {
		return rdvList;
	}

	public void setRdvList(List<Rdv> rdvList) {
		this.rdvList = rdvList;
	}

	// R�cupere les messages d'erreurs
	public String getviduser() {
		return viduser;
	}

	public String getvidpatient() {
		return vidpatient;
	}

	public String getvdate() {
		return vdate;
	}

	public String getvheure() {
		return vheure;
	}

	public String getvduree() {
		return vduree;
	}

	// La m�thode qui permet de v�rifier si les donn�es sont saisie
	public String valid() {
		// bool est un boolean de base est false si un champ et vide il se met
		// en true
		boolean bool = false;
		// si iduser=0 (vide)
		if (rdv.getIduser() == 0) {
			viduser = "Id user requis";
			bool = true;
		}
		// si idpatient=0 (vide)
		if (rdv.getIdpatient() == 0) {
			vidpatient = "Id patient requis";
			bool = true;
		}
		// si la date est pas bien format� ou vide
		if (DateUtil.isValidDate(DateUtil.getDate(rdv.getDaterdv()).toString()) == false
				|| rdv.getDaterdv().equals("")) {
			vdate = "format date incorrect ou vide";
			bool = true;
		}
		if (rdv.getDuree() == 0) {
			vduree = "Dur�e requis";
			bool = true;
		}
		if (rdv.getHour() == 0) {
			vheure = "Heure requis";
			bool = true;
		}

		// si bool est true donc l'un des champs est vide dont return input /
		// sinon success
		if (bool) {
			return INPUT;
		} else {
			return SUCCESS;
		}
	}

}
