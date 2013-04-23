package com.sopovs.moradanen.ecb.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PictureController {

    private static final int BLOCK_LENGTH = 128;
    private static final int BMP_HEADER_LENGTH = 54;
    private final Random r = new Random();
    private static final long CACHE_DURATION_IN_MS = 365 * 24 * 60 * 60 * 1000;
    private static final long PLAIN_IMAGES_CREATION_DATE;
    static {
        Calendar instance = Calendar.getInstance();
        instance.set(2013, Calendar.APRIL, 23, 23, 00);
        PLAIN_IMAGES_CREATION_DATE = instance.getTimeInMillis();
    }

    @ResponseBody
    @RequestMapping("/plain/{imagename}")
    public void plainPicture(HttpServletResponse response, @PathVariable("imagename") String imageName)
            throws IOException {

        // TODO all these cache headers do nothing - image is reloaded every
        // time
        long now = System.currentTimeMillis();

        response.addHeader("Cache-Control", "max-age=" + CACHE_DURATION_IN_MS);
        // response.addHeader("Cache-Control", "must-revalidate");// optional
        response.setDateHeader("Last-Modified", PLAIN_IMAGES_CREATION_DATE);
        response.setDateHeader("Expires", now + CACHE_DURATION_IN_MS);
        response.setContentType("image/bmp");

        InputStream input = PictureController.class.getResourceAsStream("/" + imageName + ".bmp");
        byte[] block = new byte[BLOCK_LENGTH];
        int read = input.read(block);
        while (read > 0) {
            response.getOutputStream().write(block, 0, read);
            read = input.read(block);
        }
    }

    @ResponseBody
    @RequestMapping("/encrypted/{imagename}")
    public void encryptedPicture(HttpServletResponse response, @PathVariable("imagename") String imageName)
            throws IOException {
        response.setContentType("image/bmp");

        byte[] key = new byte[BLOCK_LENGTH];
        r.nextBytes(key);
        InputStream input = PictureController.class.getResourceAsStream("/" + imageName + ".bmp");
        byte[] header = new byte[BMP_HEADER_LENGTH];
        int read = input.read(header);
        response.getOutputStream().write(header, 0, read);
        byte[] block = new byte[BLOCK_LENGTH];
        read = input.read(block);
        while (read > 0) {
            response.getOutputStream().write(xor(key, block, read));
            read = input.read(block);
        }
    }

    private static byte[] xor(byte[] key, byte[] plain, int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (key[i] ^ plain[i]);
        }
        return result;
    }
}
