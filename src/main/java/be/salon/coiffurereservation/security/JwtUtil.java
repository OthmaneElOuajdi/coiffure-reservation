package be.salon.coiffurereservation.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Utilitaire central pour la gestion des tokens JWT :
 * <ul>
 *   <li>Génération de token à partir de l’e-mail et des rôles utilisateur,</li>
 *   <li>Validation et vérification de signature,</li>
 *   <li>extraction des claims (email, rôles),</li>
 *   <li>Contrôle de la validité temporelle.</li>
 * </ul>
 *
 * <p>Implémentation basée sur la librairie <b>Auth0 Java JWT</b>.</p>
 */
@Component
public class JwtUtil {

    /** Clé secrète HMAC utilisée pour signer les tokens. */
    @Value("${app.jwt.secret}")
    private String secret;

    /** Durée de validité du token (en millisecondes). */
    @Value("${app.jwt.expiration-ms}")
    private Long expirationMs;

    /**
     * Génère un token JWT signé contenant l’e-mail de l’utilisateur et ses rôles.
     *
     * @param email e-mail de l’utilisateur (claim "sub")
     * @param roles liste des rôles (claim "roles")
     * @return le token JWT signé
     */
    public String generateToken(String email, List<String> roles) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return JWT.create()
                .withSubject(email)
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .sign(algorithm);
    }

    /**
     * Vérifie la validité et la signature d’un token JWT.
     *
     * @param token le token à vérifier
     * @return un objet {@link DecodedJWT} contenant les claims décodés
     * @throws JWTVerificationException si le token est invalide ou expiré
     */
    public DecodedJWT verifyToken(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.require(algorithm)
                .build()
                .verify(token);
    }

    /**
     * Extrait l’e-mail (sujet) du token JWT.
     *
     * @param token token signé
     * @return e-mail contenu dans le claim "sub"
     * @throws JWTVerificationException si le token est invalide
     */
    public String extractEmail(String token) {
        return verifyToken(token).getSubject();
    }

    /**
     * Extrait la liste des rôles à partir du claim "roles".
     *
     * @param token token signé
     * @return liste des rôles (peut être vide)
     * @throws JWTVerificationException si le token est invalide
     */
    public List<String> extractRoles(String token) {
        return verifyToken(token)
                .getClaim("roles")
                .asList(String.class);
    }

    /**
     * Vérifie simplement la validité du token sans lever d’exception.
     *
     * @param token token JWT à valider
     * @return {@code true} si le token est valide et non expiré, sinon {@code false}
     */
    public boolean isTokenValid(String token) {
        try {
            verifyToken(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
}
