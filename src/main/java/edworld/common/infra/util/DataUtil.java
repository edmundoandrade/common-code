package edworld.common.infra.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import edworld.common.infra.boundary.TimestampCalendar;

public abstract class DataUtil {
	public static final DateFormat FORMATO_DATA = new SimpleDateFormat("dd/MM/yyyy");
	public static final DateFormat FORMATO_DATA_ISO = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat FORMATO_DATA_HORA = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	public static final DateFormat FORMATO_DATA_HORA_ISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
	public static final DateFormat FORMATO_HORA = new SimpleDateFormat("HH:mm");
	public static final DateFormat FORMATO_TIMESTAMP = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
	public static final DateFormat FORMATO_DATA_ANIVERSARIO = new SimpleDateFormat("dd/MM");

	public static Calendar data(int dia, int mes, int ano) {
		return new GregorianCalendar(ano, mes - 1, dia);
	}

	public static Calendar dataHora(int dia, int mes, int ano, int hora, int minuto) {
		return dataHora(dia, mes, ano, hora, minuto, 0);
	}

	public static Calendar dataHora(int dia, int mes, int ano, int hora, int minuto, int segundo) {
		Calendar calendar = data(dia, mes, ano);
		calendar.set(Calendar.HOUR, hora);
		calendar.set(Calendar.MINUTE, minuto);
		calendar.set(Calendar.SECOND, segundo);
		return calendar;
	}

	public static TimestampCalendar timestamp(long momentoMS) {
		Calendar timestamp = new GregorianCalendar();
		timestamp.setTimeInMillis(momentoMS);
		return new TimestampCalendar(timestamp);
	}

	public static String dataToString(Calendar data) {
		return dataToString(data, FORMATO_DATA);
	}

	public static String dataToString(Calendar data, DateFormat formato) {
		if (data == null)
			return null;
		return formato.format(data.getTime());
	}

	public static String dataHoraToString(Calendar dataHora) {
		if (dataHora == null)
			return null;
		return FORMATO_DATA_HORA.format(dataHora.getTime());
	}

	public static String horaToString(Calendar data) {
		return dataToString(data, FORMATO_HORA);
	}

	public static String timeStampToString(Calendar timestamp) {
		if (timestamp == null)
			return null;
		return FORMATO_TIMESTAMP.format(timestamp.getTime());
	}

	public static String timeStampToString(TimestampCalendar timestamp) {
		return timeStampToString(timestamp.getTimestamp());
	}

	public static Calendar parseData(String data) {
		return parseDataHora(data.replaceAll("(\\d+)[º°]", "$1"), FORMATO_DATA);
	}

	public static Calendar parseDataISO(String data) {
		return parseDataHora(data, FORMATO_DATA_ISO);
	}

	public static Calendar parseDataHora(String dataHora) {
		return parseDataHora(dataHora, FORMATO_DATA_HORA);
	}

	public static Calendar parseDataHoraISO(String dataHora) {
		return parseDataHora(dataHora, FORMATO_DATA_HORA_ISO);
	}

	public static Calendar parseTimeStamp(String timeStamp) {
		return parseDataHora(timeStamp, FORMATO_TIMESTAMP);
	}

	public static Calendar parseDataHora(String dataHora, DateFormat formato) {
		Calendar resultado = GregorianCalendar.getInstance();
		try {
			resultado.setTime(formato.parse(dataHora));
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
		return resultado;
	}

	public static Calendar data(Calendar dataHora) {
		Calendar data = (Calendar) dataHora.clone();
		data.set(Calendar.HOUR_OF_DAY, 0);
		data.set(Calendar.MINUTE, 0);
		data.set(Calendar.SECOND, 0);
		data.set(Calendar.MILLISECOND, 0);
		return data;
	}

	public static Calendar somarDias(Calendar dataInicio, int dias) {
		Calendar data = (Calendar) dataInicio.clone();
		data.add(Calendar.DAY_OF_MONTH, dias);
		// ajustar após somar dias a uma data que inicia horário de verão
		if (data.get(Calendar.HOUR_OF_DAY) == 1)
			data.set(Calendar.HOUR_OF_DAY, 0);
		return data;
	}

	public static String formatarDuracao(long duracao) {
		long hora = (duracao / 3600000);
		if (hora == 0)
			return (duracao / 60000) + "min " + ((duracao % 60000) / 1000) + "s";
		long duracaoResidual = duracao % 3600000;
		return hora + "h" + (duracaoResidual / 60000) + "min" + ((duracaoResidual % 60000) / 1000) + "s";
	}

	public static Calendar agora() {
		return Calendar.getInstance();
	}

	public static Calendar hoje() {
		return data(agora());
	}

	public static Calendar dataMesmaSemana(Calendar data, int diaDaSemana) {
		return somarDias(data, diaDaSemana - diaDaSemana(data));
	}

	public static int diaDaSemana(Calendar data) {
		return data.get(Calendar.DAY_OF_WEEK);
	}

	public static int diaDoMes(Calendar data) {
		return data.get(Calendar.DAY_OF_MONTH);
	}

	public static int mes(Calendar data) {
		return data.get(Calendar.MONTH) + 1;
	}

	public static int ano(Calendar data) {
		return data.get(Calendar.YEAR);
	}

	public static Calendar parseDataInicioPeriodo(String periodo) {
		String inicio = extractDataInicioPeriodo(periodo);
		String fim = extractDataFimPeriodo(periodo);
		int qtdPartesDataInicio = inicio.split("/").length;
		for (int i = 0; i < qtdPartesDataInicio; i++)
			fim = fim.replaceFirst(".*?/", "");
		return DataUtil.parseDataHora(inicio + "/" + fim, new SimpleDateFormat("dd/MMMMM/yyyy"));
	}

	public static Calendar parseDataFimPeriodo(String periodo) {
		return DataUtil.parseDataHora(extractDataFimPeriodo(periodo), new SimpleDateFormat("dd/MMMMM/yyyy"));
	}

	private static String extractDataInicioPeriodo(String periodo) {
		return periodo.trim().toLowerCase().replaceAll("\\s+a\\s+.*", "").replace("º", "").replaceAll("\\s+de\\s+",
				"/");
	}

	private static String extractDataFimPeriodo(String periodo) {
		return periodo.trim().toLowerCase().replaceAll(".*\\s+a\\s+", "").replace("º", "").replaceAll("\\s+de\\s+", "/")
				.replaceAll("\\s+", "/");
	}
}
