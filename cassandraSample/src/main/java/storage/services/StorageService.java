package storage.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;

/**
 * Created by d.asadullin on 31.08.2016.
 */
@RestController()
@RequestMapping(value = "/storage")
public class StorageService {
    @Autowired
    CassandraClient client;
    final Logger logger = LoggerFactory.getLogger(StorageService.class);

    @RequestMapping(value = "/put", method = {RequestMethod.POST})
    public
    @ResponseBody
    String put(HttpEntity<byte[]> requestEntity,
               HttpServletResponse response) throws Exception {
        byte[] payload = requestEntity.getBody();
        HttpHeaders headers = requestEntity.getHeaders();
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-1");
            instance.reset();
            byte[] digest = instance.digest(payload);
            String url = client.put(digest, "", payload);
            response.setStatus(HttpStatus.OK.value());
            return url;
        } catch (Exception ex) {
            logger.error("cannot put", ex);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ex.getMessage();
        }
    }

    @RequestMapping(value = "/putPart", method = {RequestMethod.POST})
    public
    @ResponseBody
    String putPart(
            HttpEntity<byte[]> requestEntity,
            @RequestParam("hash") String hash,
            @RequestParam("parts") Integer parts,
            @RequestParam("size") Integer size,
            @RequestParam("partid") Integer partId,
            @RequestParam(value = "returnUrl",defaultValue = "False") Boolean returnUrl,
            HttpServletResponse response) throws Exception {
        byte[] payload = requestEntity.getBody();
        HttpHeaders headers = requestEntity.getHeaders();
        try {
            String resp=client.put(DatatypeConverter.parseHexBinary(hash), "", payload, partId, size, parts,returnUrl);
            response.setStatus(HttpStatus.OK.value());
            return resp;
        } catch (Exception ex) {
            logger.error("cannot put", ex);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return null;
        }
    }

    @RequestMapping(value = "/get", method = {RequestMethod.GET})
    public
    @ResponseBody
    byte[] get(@RequestParam(value = "url",required = false) String url,
               @RequestParam(value = "hash",required = false) String hash,
               HttpEntity requestEntity,
               HttpServletResponse response) throws Exception {
        try {
            if(url!=null) {
                byte[] resp = client.get(url, null);
                if (resp == null) {
                    response.setStatus(HttpStatus.NO_CONTENT.value());
                    return null;
                } else {
                    response.setStatus(HttpStatus.OK.value());
                    return resp;
                }
            }else if (hash!=null) {
                byte[] resp = client.get(DatatypeConverter.parseHexBinary(hash), null);
                if (resp == null) {
                    response.setStatus(HttpStatus.NO_CONTENT.value());
                    return null;
                } else {
                    response.setStatus(HttpStatus.OK.value());
                    return resp;
                }
            }
            return null;
        } catch (Exception ex) {
            logger.error("cannot get", ex);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return null;
        }
    }



    @RequestMapping(value = "/getPart", method = {RequestMethod.GET})
    public
    @ResponseBody
    byte[] partByHash(@RequestParam(value = "url",required = false) String url,
                      @RequestParam(value = "hash",required = false) String hash,
                      @RequestParam("partid") String part,
                      HttpEntity requestEntity,
                      HttpServletResponse response) throws Exception {
        try {
            if(url!=null) {
                byte[] resp = client.get(url, part);
                if (resp == null) {
                    response.setStatus(HttpStatus.NO_CONTENT.value());
                    return null;
                } else {
                    response.setStatus(HttpStatus.OK.value());
                    return resp;
                }
            } else if(hash!=null) {
                byte[] resp = client.get(DatatypeConverter.parseHexBinary(hash), part);
                if (resp == null) {
                    response.setStatus(HttpStatus.NO_CONTENT.value());
                    return null;
                } else {
                    response.setStatus(HttpStatus.OK.value());
                    return resp;
                }
            }
            return null;
        } catch (Exception ex) {
            logger.error("cannot get", ex);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return null;
        }
    }



    @RequestMapping(value = "/meta", method = {RequestMethod.GET})
    public
    @ResponseBody
    String getMeta(@RequestParam("url") String url,
                   HttpEntity requestEntity,
                   HttpServletResponse response) throws Exception {
        try {
            String resp = client.getMeta(url);
            if (resp == null) {
                response.setStatus(HttpStatus.NO_CONTENT.value());
                return null;
            } else {
                response.setStatus(HttpStatus.OK.value());
                return resp;
            }
        } catch (Exception ex) {
            logger.error("cannot get", ex);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return null;
        }
    }
}
