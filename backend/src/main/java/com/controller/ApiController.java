package com.controller;
import com.entity.*;
import com.repository.*;
import com.config.SecurityConfig;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;
import java.time.*;
import java.util.*;
import java.util.Locale.Category;
@RestController 
@RequestMapping("/api")
public class ApiController{
    private final UserREpository users;
    private final ProductRepository products;
    private CategoryRepository categories;
    private final ProductImageRepository images;
    private final OrderLineRepository lines;
    private final ShipmentRepository shipments;
    private final StockAlerteRepository alerts;
    private final PasswordEncoder passwords;
    private final SecurityConfig jwt;
    public ApiController(UserRepository users,ProductRepository products, CategoryRepository categories, ProductImageRepository images,OrderLineRepository lines,ShipmentRepository shipments, StockAlerteRepository alerts,PasswordEncoder passwords,SecurityConfig jwt){
        this.users=users;
        this.products=products;
        this.categories=categories;
        this.images=images;
        this.lines=lines;
        this.shipments=shipments;
        this.alerts=alerts;
        this.passwords=passwords;
        this.jwt=jwt;
    }
    record Credentials(String email,String password,String nom,String prenom){}
    record ProductForm(String nom,String description,BigDecimal prix,Integer quantiteStock,Integer seuilStockBas,Long categorieId,Boolean actif,String imageUrl){}
    record ShipmentForm(String statut,String numeroSuivi){}
    USer me(Authentication auth){if(auth==nul)throw new ResponseStatusEXception(HttpStatus.UNAUTHORIZED);return users.findByEmail(auth.getName()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED));}
    User seller(Authentication auth){
        User u=me(auth);
        if(u.role!=User.Role.VENDEUR)throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return u;
    }
    Product existing(Long id){
        return products.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND."Produit introuvable"));

    }
    @PostMapping("/auth/register") public Map<String,Object> register(@RequestBody Credentials c){if(c.email()==null||!c.email().contains("@")||c.password()==null||c.password().length()<8)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Email et mot de passe (8 caracteres min.) requis");String email=c.email().trim().toLowerCase();if(users.findByEmail(email).isPresent())throw new ResponseStatusException(HttpStatus.CONFLICT,"Email deja utilise");User u=new User();u.email=email;u.passwordHash=passwords.encode(c.password());u.nom=c.nom();u.prenom=c.prenom();u.role=User.Role.CLIENT;users.save(u);return Map.of("token",jwt.token(email),"role",u.role.name());}
 @PostMapping("/auth/login") public Map<String,Object> login(@RequestBody Credentials c){if(c.email()==null||c.password()==null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);User u=users.findByEmail(c.email().trim().toLowerCase()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Identifiants invalides"));if(!u.actif||!passwords.matches(c.password(),u.passwordHash))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Identifiants invalides");return Map.of("token",jwt.token(u.email),"role",u.role.name());}
 @GetMapping("/me") public Map<String,Object> profile(Authentication auth){User u=me(auth);return Map.of("id",u.id,"email",u.email,"nom",u.nom==null?"":u.nom,"prenom",u.prenom==null?"":u.prenom,"role",u.role.name());}
 @GetMapping("/categories") public List<Category> categories(){return categories.findAll();}
 @GetMapping("/products") public List<Product> catalogue(@RequestParam(required=false) String q,@RequestParam(required=false) Long categorie,@RequestParam(required=false) BigDecimal minPrix,@RequestParam(required=false) BigDecimal maxPrix){return products.findByActifTrue().stream().filter(p->q==null||p.nom!=null&&p.nom.toLowerCase().contains(q.toLowerCase())).filter(p->categorie==null||p.categorie!=null&&categorie.equals(p.categorie.id)).filter(p->minPrix==null||p.prix.compareTo(minPrix)>=0).filter(p->maxPrix==null||p.prix.compareTo(maxPrix)<=0).toList();}
 @GetMapping("/products/{id}") public Product detail(@PathVariable Long id){Product p=existing(id);if(!p.actif)throw new ResponseStatusException(HttpStatus.NOT_FOUND);return p;}
 @GetMapping("/products/{id}/images") public List<ProductImage> productImages(@PathVariable Long id){Product p=existing(id);return images.findAll().stream().filter(i->i.produit.id.equals(p.id)).toList();}
 @GetMapping("/seller/products") public List<Product> myProducts(Authentication auth){Long id=seller(auth).id;return products.findAll().stream().filter(p->p.vendeur!=null&&id.equals(p.vendeur.id)).toList();}
 void apply(Product p,ProductForm f){if(f.nom()==null||f.nom().isBlank()||f.prix()==null||f.prix().signum()<0||f.quantiteStock()==null||f.quantiteStock()<0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Donnees produit invalides");p.nom=f.nom().trim();p.description=f.description();p.prix=f.prix();p.quantiteStock=f.quantiteStock();p.seuilStockBas=f.seuilStockBas()==null?5:Math.max(0,f.seuilStockBas());p.actif=f.actif()==null||f.actif();p.categorie=f.categorieId()==null?null:categories.findById(f.categorieId()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Categorie inconnue"));}
 void setImage(Product p,String url){if(url==null||url.isBlank())return;if(!url.startsWith("https://"))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Image: URL HTTPS requise");ProductImage img=images.findAll().stream().filter(i->i.produit.id.equals(p.id)&&i.principale).findFirst().orElseGet(()->{ProductImage i=new ProductImage();i.produit=p;i.principale=true;return i;});img.url=url;images.save(img);}
 @PostMapping("/seller/products") @Transactional public Product create(Authentication auth,@RequestBody ProductForm f){Product p=new Product();p.vendeur=seller(auth);apply(p,f);products.save(p);setImage(p,f.imageUrl());return p;}
 @PutMapping("/seller/products/{id}") @Transactional public Product update(Authentication auth,@PathVariable Long id,@RequestBody ProductForm f){Product p=owned(auth,id);apply(p,f);products.save(p);setImage(p,f.imageUrl());return p;}
 @DeleteMapping("/seller/products/{id}") public ResponseEntity<Void> delete(Authentication auth,@PathVariable Long id){Product p=owned(auth,id);p.actif=false;products.save(p);return ResponseEntity.noContent().build();}
 @GetMapping("/seller/orders") public List<Map<String,Object>> sellerOrders(Authentication auth){Long id=seller(auth).id;return lines.findAll().stream().filter(l->l.produit!=null&&l.produit.vendeur!=null&&id.equals(l.produit.vendeur.id)).map(l->{Map<String,Object> m=new LinkedHashMap<>();m.put("ligneId",l.id);m.put("commandeId",l.commande.id);m.put("dateCommande",l.commande.dateCommande);m.put("statutCommande",l.commande.statut.name());m.put("produit",l.produit.nom);m.put("quantite",l.quantite);m.put("montant",l.prixUnitaire.multiply(BigDecimal.valueOf(l.quantite)));return m;}).toList();}
 @GetMapping("/seller/shipments") public List<Shipment> sellerShipments(Authentication auth){Long id=seller(auth).id;return shipments.findAll().stream().filter(s->s.vendeur!=null&&id.equals(s.vendeur.id)).toList();}
 @PutMapping("/seller/orders/{orderId}/shipment") @Transactional public Shipment ship(Authentication auth,@PathVariable Long orderId,@RequestBody ShipmentForm f){User u=seller(auth);boolean owns=lines.findByCommandeId(orderId).stream().anyMatch(l->l.produit!=null&&l.produit.vendeur!=null&&u.id.equals(l.produit.vendeur.id));if(!owns)throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Commande non liee au vendeur");Shipment.Statut status;try{status=Shipment.Statut.valueOf(f.statut());}catch(Exception ex){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Statut inconnu");}Shipment s=shipments.findAll().stream().filter(x->x.commande.id.equals(orderId)&&x.vendeur.id.equals(u.id)).findFirst().orElseGet(()->{Shipment x=new Shipment();x.vendeur=u;x.commande=lines.findByCommandeId(orderId).get(0).commande;return x;});s.statut=status;s.numeroSuivi=f.numeroSuivi();if(status==Shipment.Statut.EXPEDIEE)s.dateExpedition=LocalDateTime.now();return shipments.save(s);}
 @GetMapping("/seller/alerts") public List<StockAlert> alerts(Authentication auth){Long id=seller(auth).id;return alerts.findAll().stream().filter(x->x.produit.vendeur!=null&&id.equals(x.produit.vendeur.id)).toList();}
 @GetMapping("/seller/low-stock") public List<Product> lowStock(Authentication auth){return myProducts(auth).stream().filter(p->p.quantiteStock<=p.seuilStockBas&&p.actif).toList();}
 @GetMapping("/seller/stats") public Map<String,Object> stats(Authentication auth){List<Product> owned=myProducts(auth);List<Map<String,Object>> sold=sellerOrders(auth);BigDecimal revenue=sold.stream().filter(x->!"ANNULEE".equals(x.get("statutCommande"))).map(x->(BigDecimal)x.get("montant")).reduce(BigDecimal.ZERO,BigDecimal::add);return Map.of("produits",owned.size(),"commandes",sold.stream().map(x->x.get("commandeId")).distinct().count(),"chiffreAffairesEstime",revenue,"stockBas",owned.stream().filter(p->p.actif&&p.quantiteStock<=p.seuilStockBas).count());}
}
