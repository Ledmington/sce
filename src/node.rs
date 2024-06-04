use std::fmt;

#[derive(Debug, PartialEq)]
pub enum Node<'a> {
    Bracket {
        expr: Box<Node<'a>>,
    },
    Plus {
        lhs: Box<Node<'a>>,
        rhs: Box<Node<'a>>,
    },
    Minus {
        lhs: Box<Node<'a>>,
        rhs: Box<Node<'a>>,
    },
    Multiply {
        lhs: Box<Node<'a>>,
        rhs: Box<Node<'a>>,
    },
    Divide {
        lhs: Box<Node<'a>>,
        rhs: Box<Node<'a>>,
    },
    Power {
        lhs: Box<Node<'a>>,
        rhs: Box<Node<'a>>,
    },
    Constant(i64),
    Variable(&'a str),
    Function(&'a str, Box<Node<'a>>),
}

impl fmt::Display for Node<'_> {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        match self {
            Node::Bracket { expr } => write!(f, "({})", expr),
            Node::Plus { lhs, rhs } => write!(f, "{}+{}", lhs, rhs),
            Node::Minus { lhs, rhs } => write!(f, "{}+{}", lhs, rhs),
            Node::Multiply { lhs, rhs } => write!(f, "{}+{}", lhs, rhs),
            Node::Divide { lhs, rhs } => write!(f, "{}+{}", lhs, rhs),
            Node::Power { lhs, rhs } => write!(f, "{}+{}", lhs, rhs),
            Node::Constant(x) => write!(f, "{}", x),
            Node::Variable(x) => write!(f, "{}", x),
            Node::Function(n, x) => write!(f, "{}({})", n, x),
        }
    }
}
