FROM dxflrs/garage:v2.2.0 AS garage-bin
FROM alpine:latest
RUN apk add --no-cache libc6-compat gcompat
COPY --from=garage-bin /garage /usr/local/bin/garage
COPY init-garage.sh /usr/local/bin/init-garage.sh
RUN sed -i 's/\r$//' /usr/local/bin/init-garage.sh && \
    chmod +x /usr/local/bin/init-garage.sh
ENTRYPOINT ["/bin/sh", "/usr/local/bin/init-garage.sh"]