package edworld.common.core.entity;

import static edworld.common.infra.util.DataUtil.dataHoraToString;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonIgnore;

import edworld.common.infra.boundary.TimestampCalendar;

public class EntidadeVersionada {
	@XmlElement
	protected String usuario;
	@XmlElement
	protected TimestampCalendar momento;
	@XmlElement
	protected Integer versao;

	public String getUsuario() {
		return usuario;
	}

	public TimestampCalendar getMomento() {
		return momento;
	}

	public Integer getVersao() {
		return versao;
	}

	@JsonIgnore
	public String getUltimoRegistro() {
		return "Versão " + versao + " por " + usuario + " em " + dataHoraToString(momento.getTimestamp());
	}

	public void incrementarVersao() {
		versao = versao == null ? 1 : versao + 1;
	}

	public void unificarVersionamento(EntidadeVersionada entidadeVersionada) {
		this.usuario = entidadeVersionada.getUsuario();
		this.momento = entidadeVersionada.getMomento();
		this.versao = entidadeVersionada.getVersao();
	}
}
