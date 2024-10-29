package com.example.demo;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.mail.MessagingException;

@Controller
public class MyControllerPcSql {

	@Autowired
	EmailService emailService;

	pcJdbcTemplate d1;
	ArrayList<pc> listaPc = new ArrayList<>();
	ArrayList<pcComprati> pcComprati = new ArrayList<>();

	@Autowired
	public MyControllerPcSql(pcJdbcTemplate d1) {
		this.d1 = d1;
		this.listaPc = d1.getLista();
	}

	@GetMapping("/")
	public String getIndex(Model model) {
		String nome = "pc";
		model.addAttribute("nome", nome);
		return "index";
	}

	@GetMapping("/listaPcStore")
	public String getLista(Model model) {

		double somma = 0;
		model.addAttribute("somma", somma);

		listaPc = d1.getLista();
		model.addAttribute("lista", listaPc);
		return "store";
	}

	@GetMapping("/vaiMagazzino")
	public String getMagazzino(Model model) {
		listaPc = d1.getLista();
		model.addAttribute("lista", listaPc);
		return "stampaMagazzino";
	}

	@PostMapping("/gestioneMagazzino")
	public String getPc(@RequestParam("nome") String nome, @RequestParam("marca") String marca,
			@RequestParam("descrizione") String descrizione, @RequestParam("prezzo") Double prezzo,
			@RequestParam("url") String url, @RequestParam("qntMagazzino") int qntMagazzino,
			@RequestParam("qntVenduti") int qntVenduti, Model m1) {

		d1.insertPc(nome, marca, descrizione, prezzo, url, qntMagazzino, qntVenduti);

		pc pcSingolo = new pc(nome, marca, descrizione, prezzo, url, qntMagazzino, qntVenduti);
		listaPc.add(pcSingolo);

		listaPc = d1.getLista();
		m1.addAttribute("lista", listaPc);

		return "stampaMagazzino";
	}

	@PostMapping("/rimuoviDalMagazzino")
	public String rimuoviDalMagazzino(@RequestParam("nome") String nome, Model m1) {

		listaPc.removeIf(pc -> pc.getNome().equals(nome));

		d1.delete(nome);

		m1.addAttribute("lista", listaPc);
		return "redirect:/vaiMagazzino";
	}

	@PostMapping("/aggiungiCarrello")
	public String compra(@RequestParam("nome") String nome, @RequestParam("num") int num) {

		boolean found = false;

		if (num > 0)

			for (pc pc : listaPc) {

				if (pc.getNome().equals(nome)) {
					if (pc.qntMagazzino >= num) {
						for (pcComprati pcC : pcComprati) {

							if (pcC.getNome().equals(nome)) {

								pcC.setQnt(pcC.getQnt() + num);
								pcC.setQntMagazzino(pcC.getQntMagazzino());
								pcC.setQntVenduti(pcC.getQntVenduti() + num);
								found = true;
								break;

							}
						}
						if (!found) {
							pcComprati pcAcquistato = new pcComprati(pc.getNome(), pc.getMarca(), pc.getDescrizione(),
									pc.getPrezzo(), pc.getUrl(), pc.getQntMagazzino(), pc.getQntVenduti() + num, num);

							pcComprati.add(pcAcquistato);
						}

					} else {

						return "Quantità richiesta non disponibile";

					}
				}
			}

		return "redirect:/funzioneCarrello";

	}

	@PostMapping("/rimuoviDalCarrello")
	public String rimuoviDalCarrello(@RequestParam("nome") String nome, Model m1) {
		System.out.println("Rimuovendo dal carrello: " + nome);
		boolean found = false;

		for (pcComprati pcC : pcComprati) {
			if (pcC.getNome().equals(nome)) {

				if (pcC.getQnt() > 1) {

					pcC.setQnt(pcC.getQnt() - 1);
					found = true;

				}
				break;

			}
		}

		if (!found) {
			boolean removed = pcComprati.removeIf(pc -> pc.getNome().equals(nome));
			if (removed) {
				System.out.println(nome + " è stato rimosso dal carrello.");
			} else {
				System.out.println(nome + " non è stato trovato nel carrello.");
			}
		}

		return "redirect:/funzioneCarrello";
	}

	ArrayList<pcComprati> listaCarrello = new ArrayList<>();

	@GetMapping("/funzioneCarrello")
	public String stampaCarrello(Model m1) {
		double somma = 0;

		listaCarrello.clear();
		for (pcComprati pc : pcComprati) {
			somma += pc.getPrezzo() * pc.getQnt();
			listaCarrello.add(pc);
		}

		m1.addAttribute("lista", listaPc);

		m1.addAttribute("somma", somma);
		m1.addAttribute("carrello", listaCarrello);
		return "store";
	}

	@PostMapping("/cambiaQnt")
	public String rimuovi

	(@RequestParam("nome") String nome, @RequestParam("num") int num) {
		for (pc pc : listaPc) {
			if (pc.getNome().equals(nome)) {
				for (pcComprati pc1 : pcComprati) {
					if (pc1.getNome().equals(nome) && pc.qntMagazzino >= num) {
						pc1.setQnt(num);
						break;
					}
				}
			}
		}
		return "redirect:/funzioneCarrello";
	}

	@PostMapping("/svuotaCarrello")
	public String svuotaCarrello() {
		pcComprati.clear();
		return "redirect:/funzioneCarrello";
	}

	@PostMapping("/confermaAcquisto")
	public String confermaAcquisto(Model m1) {

		if (pcComprati == null || pcComprati.isEmpty()) {

			m1.addAttribute("messaggio", "Il carrello è vuoto. Non puoi confermare l'acquisto.");

			m1.addAttribute("lista", listaPc);
			return "store";
		}

		double somma = 0;
		for (pcComprati pc : pcComprati) {
			somma += pc.getPrezzo() * pc.getQnt();
			// metodo getQnt appartiene a pcComprati
		}
		m1.addAttribute("somma", somma);
		m1.addAttribute("lista", pcComprati);

		return ("confermaAcquisto");
	}

	@ResponseBody
	@PostMapping("/Recap")
	public String getResoconto(@RequestParam("mail") String mail) throws MessagingException {

		ArrayList<String> url = new ArrayList<>();
		ArrayList<String> names = new ArrayList<>();
		ArrayList<String> quant = new ArrayList<>();

		String recap = "";
		double somma = 0;
		for (pcComprati pc : listaCarrello) {
			somma += pc.getPrezzo() * pc.getQnt();

			d1.updateQnt(pc.getQnt(), pc.nome);
			d1.updateQntMagazzino(pc.getQnt(), pc.nome);

			url.add(pc.url);
			names.add(pc.nome);

		}

		recap += "Totale da pagare  :" + somma;

		emailService.sendEmailWithImage(mail, "mail dallo stor pc", recap, url, names);

		return ("Acquisto avvenuto con successo");

	}

}
