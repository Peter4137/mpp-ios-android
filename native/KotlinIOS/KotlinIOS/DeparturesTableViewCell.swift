import UIKit

class DeparturesTableViewCell: UITableViewCell {

    @IBOutlet weak var departureLabel: UILabel!
    @IBOutlet weak var arrivalLabel: UILabel!
    @IBOutlet weak var durationLabel: UILabel!
    @IBOutlet weak var priceLabel: UILabel!
    @IBOutlet weak var operatorLabel: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        self.accessibilityElements = [self.departureLabel, self.arrivalLabel, self.durationLabel, self.priceLabel, self.operatorLabel]
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
        // Configure the view for the selected state
    }

}
