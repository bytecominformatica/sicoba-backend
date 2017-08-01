package br.com.clairtonluz.sicoba.service.financeiro.gerencianet.notification;

import br.com.clairtonluz.sicoba.model.entity.comercial.Consumer;
import br.com.clairtonluz.sicoba.model.entity.comercial.StatusConsumer;
import br.com.clairtonluz.sicoba.model.entity.financeiro.gerencianet.carnet.Carnet;
import br.com.clairtonluz.sicoba.model.entity.financeiro.gerencianet.carnet.StatusCarnet;
import br.com.clairtonluz.sicoba.model.entity.financeiro.gerencianet.charge.Charge;
import br.com.clairtonluz.sicoba.model.entity.financeiro.gerencianet.charge.StatusCharge;
import br.com.clairtonluz.sicoba.repository.financeiro.gerencianet.CarnetRepository;
import br.com.clairtonluz.sicoba.repository.financeiro.gerencianet.ChargeRepository;
import br.com.clairtonluz.sicoba.service.comercial.ConsumerService;
import br.com.clairtonluz.sicoba.service.financeiro.gerencianet.GNService;
import br.com.clairtonluz.sicoba.util.SendEmail;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by clairton on 20/12/16.
 */
@Service
public class NotificationService {

    @Autowired
    private NotificationGNService notificationGNService;
    @Autowired
    private ChargeRepository chargeRepository;
    @Autowired
    private CarnetRepository carnetRepository;
    @Autowired
    private ConsumerService consumerService;

    @Transactional
    public void processNotification(String token) {
        JSONObject response = notificationGNService.getNotification(token);
        if (GNService.isOk(response)) {
            JSONArray data = response.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject it = data.getJSONObject(i);
                switch (it.getString("type")) {
                    case "charge":
                        processarCharge(token, it);
                        break;
                    case "carnet":
                        processarCarnet(token, it);
                        break;
                    case "carnet_charge":
                        processarCarnetCharge(token, it);
                        break;
                    default:
                        Logger.getLogger(getClass().getName()).warning(it.toString());
                }
            }
        } else {
            SendEmail.sendToAdmin("[NOTIFICATION] Token não encontrado", String.format("token:%s\ncontent:%s", token, String.valueOf(response)));
        }
    }

    private void processarCharge(String token, JSONObject data) {
        Charge charge = chargeRepository.findOptionalByChargeId(data.getJSONObject("identifiers").getInt("charge_id"));
        if (charge == null) {
            SendEmail.sendToAdmin("[NOTIFICATION] Cobrança não processada", "Cobrança não processado:" + data.toString());
            return;
        }

        int id = data.getInt("id");
        if (charge.getLastNotification() == null || id > charge.getLastNotification()) {
            charge.setTokenNotification(token);
            charge.setLastNotification(id);
            JSONObject status = data.getJSONObject("status");
            charge.setStatus(StatusCharge.valueOf(status.getString("current").toUpperCase()));

            if (charge.getStatus().equals(StatusCharge.PAID)) {
                charge.setPaidValue(data.getDouble("value") / 100);
                charge.setPaidAt(new Date());

                Consumer consumer = charge.getConsumer();
                String messageVerify = charge.verifyPayment();
                if (!messageVerify.equals(Charge.VALID_PAYMENT)) {
                    try {
                        consumerService.inativar(consumer);
                        String subject = String.format("Consumer %s bloqueado por pagamento inválido", consumer.getName());
                        String chargeJson = new ObjectMapper().writeValueAsString(charge);
                        String message = String.format("Cobrana: %s\nMotivo:%s", chargeJson, messageVerify);
                        SendEmail.sendToAdmin(subject, message);
                    } catch (Exception e) {
                        e.printStackTrace();
                        String subject = String.format("Não foi possível bloquear o consumer %s por pagamento inválido do tipo:%s", consumer.getName(), messageVerify);
                        SendEmail.notificarAdmin(subject, e);
                    }
                } else if (consumer.getStatus().equals(StatusConsumer.INATIVO)) {
                    try {
                        consumerService.ativar(consumer);
                    } catch (Exception e) {
                        e.printStackTrace();
                        String subject = String.format("Não foi possível ativar o consumer %s", consumer.getName());
                        SendEmail.notificarAdmin(subject, e);
                    }
                }
            }

            chargeRepository.save(charge);
        }
    }

    private void processarCarnet(String token, JSONObject data) {
        Carnet carnet = carnetRepository.findOptionalByCarnetId(data.getJSONObject("identifiers").getInt("carnet_id"));
        if (carnet == null) {
            SendEmail.sendToAdmin("[NOTIFICATION] Carnê não processado", "Carnê não processado:" + data.toString());
            return;
        }

        int id = data.getInt("id");
        if (carnet.getLastNotification() == null || id > carnet.getLastNotification()) {
            carnet.setTokenNotification(token);
            carnet.setLastNotification(id);
            JSONObject status = data.getJSONObject("status");
            carnet.setStatus(StatusCarnet.valueOf(status.getString("current").toUpperCase()));

            carnetRepository.save(carnet);
        }
    }

    private void processarCarnetCharge(String token, JSONObject data) {
        Charge charge = chargeRepository.findOptionalByChargeId(data.getJSONObject("identifiers").getInt("charge_id"));
        if (charge == null) {
            SendEmail.sendToAdmin("[NOTIFICATION]Parcela do carnê não processado", "Parcela carnê não processado:" + data.toString());
            return;
        }

        int id = data.getInt("id");
        if (charge.getLastNotification() == null || id > charge.getLastNotification()) {
            charge.setTokenNotification(token);
            charge.setLastNotification(id);
            JSONObject status = data.getJSONObject("status");
            charge.setStatus(StatusCharge.valueOf(status.getString("current").toUpperCase()));

            if (charge.getStatus().equals(StatusCharge.PAID)) {
                charge.setPaidValue(data.getDouble("value") / 100);
                charge.setPaidAt(new Date());
            }

            chargeRepository.save(charge);
        }
    }

}
